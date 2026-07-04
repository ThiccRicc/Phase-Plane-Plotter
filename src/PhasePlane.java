import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.script.ScriptException;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Map;


public class PhasePlane extends JPanel implements Runnable{
   
    Thread gameThread;
    static int sSize = 750;


    // JTextField
    static JTextField[] input;


    // Buttons
    static JButton solveButton;
    static JButton initialCButton;
    static JButton methodButton;


    // Sliders
    static JSlider unitSlider;


    // JLabels
    JLabel[] answerLabels;
    JLabel cLabel;


    //variables
    int unit;
    int scale;
    int[] graph; //x,y,size
    double alpha;double beta;
    Matrix A;
    Matrix AA;
    static Matrix[] ev;
    int[] tPos;
    int[] vectors;
    boolean initC;
    boolean entered;
    boolean drawVectorField;
    boolean drawS;
    boolean isReal;
    boolean methodType;
    Matrix initCondVals;
    Matrix constants;
    String f;
    String g;
    double lam1;double lam2;
    BufferedImage image;


    public PhasePlane(){
        this.setPreferredSize(new Dimension(sSize, sSize));
        this.setBackground(Color.BLACK);
        gameThread = new Thread(this);
        gameThread.start();
        this.setFocusable(true);
        this.setLayout(null);


        unit = 20;
        scale = 5;
        graph = new int[]{sSize/2,(sSize/2)+70,sSize-200};
        ev = new Matrix[2];
        initC = false;
        entered = true;
        vectors = new int[]{20,10};
        drawVectorField = false;
        drawS = false;
        AA = null;
        methodType = true;


        tPos = new int[]{80,50,40,20};
        input = new JTextField[4];
        for(int i=0;i<4;i++){
            input[i] = new JTextField(4);
            input[i].setBounds(tPos[0]+((i%2)*(tPos[2]+40)),tPos[1]+(i>1?(tPos[3]+10):(0)),tPos[2],tPos[3]);
            this.add(input[i]);
        }


        solveButton = new JButton("solve");
        solveButton.addActionListener(e -> {entered=true;if(methodType)solve();drawVectorField = true;drawS = initC == true?true:false;});
        solveButton.setBounds(340,15,70,20);
        this.add(solveButton);


        initialCButton = new JButton("Initial conditions?");
        initialCButton.addActionListener(e -> {if(initC==false){initCondVals = new Matrix(2,1,null,new double[]{Double.parseDouble(JOptionPane.showInputDialog("x(0) = ")),Double.parseDouble(JOptionPane.showInputDialog("y(0) = "))});}else{initCondVals=null;constants=null;}initC = initC==false?true:false;});
        initialCButton.setBounds(tPos[0]+10,15,140,20);
        this.add(initialCButton);


        methodButton = new JButton("switch to numerical");
        methodButton.addActionListener(e -> {methodType = !methodType;methodButton.setText(methodType ? "switch to numerical" : "switch to analytical");if(methodType){input[0].setSize(tPos[2],tPos[3]);input[2].setSize(tPos[2],tPos[3]);this.add(input[1]);this.add(input[3]);}else{input[0].setSize(120,tPos[3]);input[2].setSize(120,tPos[3]);this.remove(input[1]);this.remove(input[3]);}});
        methodButton.setBounds(20,110,200,30);
        this.add(methodButton);


        unitSlider = new JSlider(0, 5, 100, 20);
        unitSlider.setBounds(400, 80, 100, 20);
        this.add(unitSlider);


        answerLabels = new JLabel[3];
        answerLabels[0] = new JLabel();
        this.add(answerLabels[0]);
        answerLabels[0].setVisible(true);
        answerLabels[1] = new JLabel();
        this.add(answerLabels[1]);
        answerLabels[1].setVisible(true);
        answerLabels[2] = new JLabel();
        this.add(answerLabels[2]);
        answerLabels[2].setVisible(true);
        cLabel = new JLabel();
        this.add(cLabel);
        cLabel.setVisible(false);


    }


    @Override
    public void run() {
        while(gameThread != null){
            update();
            repaint();
        }


        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        throw new UnsupportedOperationException("Unimplemented method 'run'");
    }


    public void update(){
       
    }


    public void paintComponent(Graphics graph) {
        super.paintComponent(graph);
        Graphics2D g = (Graphics2D) graph;
   
        setUp(g);

        if(methodType)
            drawMatrix(g);
        else{
            setEquations();
        }

        if(entered){
            if(methodType){
                answer();
                AA = A;
            }
            entered = false;
        }


        initialConditions(g);


        vectorField(g);


        drawSolution(g);
    }


    public void setUp(Graphics2D g){
        g.setColor(Color.WHITE);


        //height = sSize-200, width = sSize-200, origin = sSize/2 (center in x) and sSize/2 + 70 (lower in y)


        int top = graph[1]-(graph[2]/2);
        int left = graph[0]-(graph[2]/2);
       
        g.fillRect(graph[0]-2,top,4,graph[2]);
        g.fillRect(left,graph[1]-2,graph[2],4);


        // for(int i=1;i*unit+graph[2]/2<graph[2];i++){
        //     int a = i*unit;
        //     g.drawLine(graph[0]+a,top,graph[0]+a,top+graph[2]);
        //     g.drawLine(graph[0]-a,top,graph[0]-a,top+graph[2]);
        //     g.drawLine(left,graph[1]+a,left+graph[2],graph[1]+a);
        //     g.drawLine(left,graph[1]-a,left+graph[2],graph[1]-a);
        // }


        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 18));


        int h = 15;int w = 60;int sp = 5;int wy = 50;
        g.drawString("x'(t) = ", tPos[0]-w, tPos[1]+h);
        g.drawString("y'(t) = ", tPos[0]-w, tPos[1]+tPos[3]+10+h);

        if(methodType){
            g.drawString("x + ", tPos[0]+sp+tPos[2], tPos[1]+h);
            g.drawString("x + ", tPos[0]+sp+tPos[2], tPos[1]+tPos[3]+10+h);
            g.drawString("y", tPos[0]+(wy+2*tPos[2]), tPos[1]+h);
            g.drawString("y", tPos[0]+(wy+2*tPos[2]), tPos[1]+tPos[3]+10+h);
        }

        g.setColor(Color.BLUE);

        if(methodType){
            g.drawString("x' = Ax",tPos[0]-60,30);
            arrow(g,tPos[0]-60,15,0,2,true);
            arrow(g,tPos[0]-11,15,0,2,true);
        }

        g.setColor(Color.WHITE);


        unit = unitSlider.getValue();
    }


    private void drawMatrix(Graphics2D g){
        g.setColor(Color.BLUE);
        g.setFont(new Font("Arial", Font.BOLD, 18));


        g.drawString("A = ",250,82);


        int[] B = new int[4];
        for(int i=0;i<4;i++){
            try {
                B[i] = Integer.parseInt(input[i].getText());
            }
            catch (NumberFormatException nfe) {
                //throw new NumberFormatException();
            }
           
        }


        A = new Matrix(2, 2, B, null);
        A.drawMatrix(g,300,tPos[1],13,Color.BLUE);
    }

    private void setEquations(){
        f = input[0].getText();
        g = input[2].getText();
    }

    private void solve(){
        int[]B = new int[4];
        for(int i=0;i<4;i++){
            try {
                B[i] = Integer.parseInt(input[i].getText());
            } catch (NumberFormatException e) {
                B[i] = 0;
                System.out.println("Integers only (for now)");
            }
           
        }


        A = new Matrix(2, 2, B, null);


        int[] poly = new int[]{
            1,
            (-1*A.a)+(-1*A.d),
            A.determinant()
        };


        //quadratic formula
        int s = poly[1]*poly[1]-(4*poly[0]*poly[2]);


        //Real eigen values
        if(s>=0){
            isReal = true;
            lam1 = round(((-1*poly[1])-Math.sqrt(s))/(2*poly[0]));
            lam2 = round(((-1*poly[1])+Math.sqrt(s))/(2*poly[0]));


            //eigen vectors
            ev[0] = new Matrix(2, 1, null, A.eigenVector(lam1));
            if(s>0){
                ev[1] = new Matrix(2, 1, null, A.eigenVector(lam2));
            }
            else{
                ev[1] = new Matrix(2, 1, null, new double[] {0, ev[0].da/A.b});
            }


            System.out.println("eigenvalues: "+lam1+", "+lam2+"; eigenvectors: ["+ev[0].da+", "+ev[0].dc+"] and ["+ev[1].da+", "+ev[1].dc+"]");
       
            //initial conditions
            if(initC == true){
                solveInitialConditions(initCondVals);
                System.out.println("C1 = "+constants.dVal("00")+", C2 = "+constants.dVal("10"));
            }
        }
        //Complex eigen values
        else{
            isReal = false;
            alpha = (-1.0*poly[1])/(2*poly[0]);
            beta = ((Math.sqrt(Math.abs(s)))/(2*poly[0]));


            System.out.println("alpha: "+alpha+", beta: +/-"+beta);

            //eigen vectors
            double[] comps;
            // if(alpha == Math.floor(alpha)){
            //     comps = new double[]{
            //         -1*lcm((int)(A.a-alpha),A.b)/(A.a-alpha),
            //         lcm((int)(A.a-alpha),A.b)/A.b,
            //         -1*beta/(A.a-alpha)
            //     }; // eigen vector components \/ \/ \/ \/ \/
            // }
            //else{
                comps = new double[]{
                    1,
                    (A.a-alpha)/(-1*A.b),
                    -1*beta/(-1*A.b)
                }; // eigen vector components \/ \/ \/ \/ \/
            //}

            ev[0] = new Matrix(2, 1, null, new double[]{comps[0],comps[1]});
            ev[1] = new Matrix(2, 1, null, new double[]{0,comps[2]});


            System.out.println("eigen vector(R): ["+ev[0].dVal("00")+", "+ev[0].dVal("10")+"] eigen vector(I): ["+ev[1].dVal("00")+", "+ev[1].dVal("10")+"]");


            if(initC == true){
                solveInitialConditions(initCondVals);
                System.out.println("C1 = "+constants.dVal("00")+", C2 = "+constants.dVal("10"));
            }
           
        }
    }


    private double[] solve2(double x, double y){
        Map<String, Double> variables = Map.of(
            "x", x,
            "y", y
        );

        double resultX = ExpressionEvaluator.evaluate(f, variables);
        double resultY = ExpressionEvaluator.evaluate(g, variables);

        //System.out.println(resultX + ", " + resultY);
        return new double[]{resultX,resultY};
    }



    private void solveInitialConditions(Matrix initialConditionValues){
        constants = new Matrix(2, 2, null, new double[]{
            ev[0].dVal("00"),
            ev[1].dVal("00"),
            ev[0].dVal("10"),
            ev[1].dVal("10")
        });
        //constants.p();
        constants = constants.dInvMatrix(constants);
        //constants.p();
        constants = constants.mult(initialConditionValues);
        //constants.p();
       
    }


    private void answer(){
        cLabel.setVisible(false);
        cLabel.setText("");


        if(ev[0]!=null && initC == true){
            Matrix[] sols = solutionMatrix();


            int[] locs = new int[4];
            locs[0] = 430;locs[1] = 30;locs[2] = 110;locs[3] = 100;
           
            answerLabels[0].setBounds(locs[0],10,locs[1],50);
            answerLabels[0].setText("<html>"+
                "<head>"+"<style>"+


                    "body {background-color: powderblue;}p    {color: red;font-size: 20}"+


                    ".subscript {"+
                        "font-size: 15; /* Adjust size as needed */"+
                        "vertical-align: bottom; /* Align as subscript */"+
                    "}"+


                "</style></head>"+


                "<p><span>x&#x20D7;</span> = </p>"+


            "</html>"
            );


            matrixImage(sols[0],8,Color.CYAN);


            answerLabels[1].setIcon(new ImageIcon(image));
            answerLabels[1].setBounds(locs[0]+locs[1],10,locs[2],50);


            answerLabels[1].setText("<html>"+
            "<head><style>"+
                "body {background-color: powderblue;}p    {color: red;font-size: 20}"+
                ".subscript {"+
                    "font-size: 15; /* Adjust size as needed */"+
                    "vertical-align: bottom; /* Align as subscript */"+
                "}"+
            "</style></head>"+


            "<p>e<sup>"+String.valueOf(lam1)+"t</sup> + </p>"+


            "</html>"
            );


            matrixImage(sols[1],8,Color.CYAN);


            answerLabels[2].setIcon(new ImageIcon(image));
            answerLabels[2].setBounds(locs[0]+locs[1]+locs[2],10,locs[3],50);


            answerLabels[2].setText("<html>"+
            "<head><style>"+
                "body {background-color: powderblue;}p    {color: red;font-size: 20}"+
                ".subscript {"+
                    "font-size: 15; /* Adjust size as needed */"+
                    "vertical-align: bottom; /* Align as subscript */"+
                "}"+
            "</style></head>"+


            "<p>"+(lam1==lam2?"x":"")+"e<sup>"+String.valueOf(lam2)+"t</sup></p>"+


            "</html>"
            );


            cLabel.setText("<html>"+
                "<head><style>"+
                "body {background-color: powderblue;}p    {color: orange;font-size: 15}"+
                ".subscript {"+
                    "font-size: 10; /* Adjust size as needed */"+
                    "vertical-align: bottom; /* Align as subscript */"+
                "}"+
                "</style></head>"+


                "<p>C<sub>1</sub> = "+round(constants.dVal("00"))+"<br>C<sub>2</sub> = "+round(constants.dVal("10"))+"</p>"+


            "</html>"
            );
       
            cLabel.setBounds(locs[0]+locs[1]+locs[2]+locs[3],10,sSize-this.getX(),50);
            cLabel.setVisible(true);


        }


        else if(ev[0]!=null){
            int[] locs = new int[4];
            locs[0] = 430;locs[1] = 60;locs[2] = 150;locs[3] = 100;
           
            answerLabels[0].setBounds(locs[0],10,locs[1],50);
            answerLabels[0].setText("<html>"+
                "<head>"+"<style>"+


                    "body {background-color: powderblue;}p    {color: red;font-size: 20}"+


                    ".subscript {"+
                        "font-size: 15; /* Adjust size as needed */"+
                        "vertical-align: bottom; /* Align as subscript */"+
                    "}"+


                "</style></head>"+


                "<p><span>x&#x20D7;</span> = C<sub class='subscript'>1</sub></p>"+


            "</html>"
            );


            matrixImage(ev[0],8,Color.CYAN);
           
            answerLabels[1].setIcon(new ImageIcon(image));
            answerLabels[1].setBounds(locs[0]+locs[1],10,locs[2],50);


            answerLabels[1].setText("<html>"+
            "<head><style>"+
                "body {background-color: powderblue;}p    {color: red;font-size: 20}"+
                ".subscript {"+
                    "font-size: 15; /* Adjust size as needed */"+
                    "vertical-align: bottom; /* Align as subscript */"+
                "}"+
            "</style></head>"+


            "<p>e<sup>"+String.valueOf(lam1)+"t</sup> + C<sub class='subscript'>2</sub></p>"+


            "</html>"
            );


            matrixImage(ev[1],8,Color.CYAN);


            answerLabels[2].setIcon(new ImageIcon(image));
            answerLabels[2].setBounds(locs[0]+locs[1]+locs[2],10,locs[3],50);


            answerLabels[2].setText("<html>"+
            "<head><style>"+
                "body {background-color: powderblue;}p    {color: red;font-size: 20}"+
                ".subscript {"+
                    "font-size: 15; /* Adjust size as needed */"+
                    "vertical-align: bottom; /* Align as subscript */"+
                "}"+
            "</style></head>"+


            "<p>"+(lam1==lam2?"x":"")+"e<sup>"+String.valueOf(lam2)+"t</sup></p>"+


            "</html>"
            );


        }


        else{
            answerLabels[0].setBounds(430,10,300,50);
            answerLabels[0].setText("<html>"+
            "<head>"+
            "<style>"+
                "body {background-color: powderblue;}h1   {color: blue;}p    {color: red;font-size: 20}"+
           
                ".minisuperscript {"+
                    "font-size: 13; /* Adjust size as needed */"+
                    "vertical-align: top; /* Align as superscript */"+
                "}"+
                ".minisubscript {"+
                    "font-size: 13; /* Adjust size as needed */"+
                    "vertical-align: bottom; /* Align as subscript */"+
                "}"+
                ".subscript {"+
                    "font-size: 15; /* Adjust size as needed */"+
                    "vertical-align: bottom; /* Align as subscript */"+
                "}"+


                ".vector {"+
                    "display: inline-block;"+
                    "position: relative;"+
                "}"+
           
            "</style></head>"+


            "<p>"+


                "<span class='vector'>x&#x20D7;</span>"+


                " = C<sub class='subscript'>1</sub>"+
                "<span class='vector'>v&#x20D7;</span><sub class='subscript'>1</sub>"+
                "e<sup>\u03bb<sub class='minisubscript'>1</sub>t</sup>"+


                " + C<sub class='subscript'>2</sub>"+
                "<span class='vector'>v&#x20D7;</span><sub class='subscript'>2</sub>"+
                "e<sup>\u03bb<sub class='minisubscript'>2</sub>t</sup>"+


            "</p>"+
            "</html>"
            );
           
        }


       
    }


    private void matrixImage(Matrix C, int size, Color c){
        image = new BufferedImage((size*C.cols*2)+21, (size*C.rows*2)+1, BufferedImage.TYPE_INT_ARGB);


        Graphics2D g2d = image.createGraphics();
        C.drawMatrix(g2d, 10, 0, size, c);
        g2d.dispose();


    }


    private void arrow(Graphics2D g, int x, int y, int a, int mag, boolean text){
        a=a<0?a+360:a;
        double[] len = {x+(mag*Math.cos(Math.PI*a/180))*scale,y-(mag*Math.sin(Math.PI*a/180))*scale};
        g.drawLine(x, y, (int)len[0], (int)len[1]);


        if(text == true){
            //upper arrow head
            g.drawLine((int)len[0],(int)len[1],(int)(len[0]+(5*Math.cos(Math.PI*(a+150)/180))),(int)(len[1]-(5*Math.sin(Math.PI*(a+150)/180))));
        }
        else{
            //upper arrow head
            g.drawLine((int)len[0],(int)len[1],(int)(len[0]+(5*Math.cos(Math.PI*(a+150)/180))),(int)(len[1]-(5*Math.sin(Math.PI*(a+150)/180))));


            //lower arrow head
            g.drawLine((int)len[0],(int)len[1],(int)(len[0]+(5*Math.cos(Math.PI*(a-150)/180))),(int)(len[1]-(5*Math.sin(Math.PI*(a-150)/180))));
        }
       
    }


    private void vectorField(Graphics2D g){
        // graph[0] = x position of origin, graph[1] = y position of origin, graph[2] = width and height
        if(drawVectorField){
            g.setColor(Color.CYAN);
            for(int i=0;i<graph[2]/(2*vectors[0]);i++){ // draw left-right
                for(int j=0;j<graph[2]/(2*vectors[0]);j++){ // draw up-down
                    int l = j==-1 ? 2 : 4;
                    for(int k=0;k<l;k++){ // draw across quadrants
                        double[] v = {((Math.pow(-1,(k==0||k==1)?0:1))*unit*i)/unit,((Math.pow(-1,k))*unit*j)/unit};

                        Matrix sols2;

                        if(methodType){
                            solveInitialConditions(new Matrix(2,1,null,v));


                            Matrix[] sols = solutionMatrix();
                            sols2 = new Matrix(2,1,null,new double[]{sols[0].dVal("00")+sols[1].dVal("00"),sols[0].dVal("10")+sols[1].dVal("10")});
                            sols2 = AA.mult(sols2);
                            // sols[0].p();
                            // sols[1].p();
                        }

                        else{
                            double[] sols = solve2(v[0],v[1]);
                            sols2 = new Matrix(2, 1, null, new double[]{sols[0],sols[1]});
                        }

                        arrow(g,(int)v[0]*unit+graph[0],(int)v[1]*-1*unit+graph[1],(int)(Math.toDegrees(Math.atan2(sols2.dVal("10"),sols2.dVal("00")))),5,false);


                    }
                }
            }
        }
    }


    private Matrix[] solutionMatrix(){
        Matrix[] sols = {
            new Matrix(2, 1, null, new double[]{
                ev[0].dVal("00")*constants.dVal("00"),
                ev[0].dVal("10")*constants.dVal("00")
            }),
            new Matrix(2, 1, null, new double[]{
                ev[1].dVal("00")*constants.dVal("10"),
                ev[1].dVal("10")*constants.dVal("10")
            })
        };


        return sols;
    }


    public void initialConditions(Graphics2D g){
        if(initC == true){
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("x(0) = ",tPos[0]+160,30);
            arrow(g,tPos[0]+160,15,0,2,true);
            initCondVals.drawMatrix(g, tPos[0]+225, 10, 8, Color.GREEN);   
        }
    }


    private void drawSolution(Graphics2D g){
        if(initCondVals != null && drawS){  


            //System.out.println("lam1 = " + lam1 + "lam2 = " + lam2);
            Matrix[] sols = new Matrix[2];
            double t = -8.0;
            int step = 20;
            int num = ((int)t*-2)*step;
            int[] xLine = new int[num*2];
            int[] yLine = new int[num*2];
            
            if(methodType){
                if(isReal) solveInitialConditions(initCondVals);
                else solveInitialConditions(new Matrix(2, 1, null, new double[]{initCondVals.da * unit,initCondVals.dc * unit}));
                sols = solutionMatrix();
            }
            else{
                sols[0] = new Matrix(2,1,null,solve2(initCondVals.da,initCondVals.dc));
            }
            //System.out.println("coeff 1: "+sols[0].da + "coeff 2: "+sols[1].da);
            //System.out.println("coeff 1: "+sols[0].dc + "coeff 2: "+sols[1].dc);


            for(int i = -1*num;i<num;t+=(1.0/step),i++){

                if(methodType){

                    if(isReal && lam1 != lam2){
                        xLine[i+num] = (int) (graph[0] + ((sols[0].da * Math.exp(lam1 * t))*unit) + ((sols[1].da * Math.exp(lam2 * t))*unit));
                        yLine[i+num] = (int) (graph[1] - ((sols[0].dc * Math.exp(lam1 * t))*unit) - ((sols[1].dc * Math.exp(lam2 * t))*unit));
                    }
                    else if(isReal && lam1 == lam2){
                        xLine[i+num] = (int) (graph[0] + ((sols[0].da * Math.exp(lam1 * t))*unit) + ((constants.dc * ev[0].da * t * Math.exp(lam1 * t))*unit));
                        yLine[i+num] = (int) (graph[1] - ((sols[0].dc * Math.exp(lam1 * t))*unit) - ((constants.dc * ev[0].dc * t * Math.exp(lam1 * t))*unit) - ((sols[1].dc * Math.exp(lam1 * t))*unit));
                    }
                    else{
                        xLine[i+num] = graph[0] + (int) ((((constants.da * Math.exp(alpha * t)) * (ev[0].da * Math.cos(beta*t)) ) + ((constants.dc * Math.exp(alpha * t)) * (ev[0].da * Math.sin(beta*t)) ))*unit);
                        yLine[i+num] = graph[1] - (int) ((((constants.da * Math.exp(alpha * t)) * ((ev[0].dc * Math.cos(beta*t)) - (ev[1].dc * Math.sin(beta*t))) ) + ((constants.dc * Math.exp(alpha * t)) * ((ev[1].dc * Math.cos(beta*t)) + (ev[0].dc * Math.sin(beta*t))) ))*unit);
                    }
                }
                else{
                    //Use Euler/Runge-Kutta method here
                    //xLine[i+num] = (int) (graph[0] + ((sols[0].da * Math.exp(lam1 * t))*unit) + ((sols[1].da * Math.exp(lam2 * t))*unit));
                    //yLine[i+num] = (int) (graph[1] - ((sols[0].dc * Math.exp(lam1 * t))*unit) - ((sols[1].dc * Math.exp(lam2 * t))*unit));
                }



                //System.out.println("t = " + t + ", " + constants.dc + ", " + ev[0].dc + " " + (xLine[i+num]-graph[0]) + ", " + (graph[1]-yLine[i+num]));


            }


            g.setColor(Color.GREEN);
            g.drawPolyline(xLine, yLine, num*2);
            //g.drawLine(xLine[0], yLine[0], xLine[5], yLine[5]);
        }
    }


    private double round(double d){
        return (Math.round(d*100))/100.0;
    }
   
    private int lcm(int a, int b) { return a * (b / gcd(a, b)); }
    private int gcd(int a, int b) { return b==0 ? a : gcd(b, a%b); }
    private int reduce(double[] d){
       
        for(double a:d){
            if((int)a != a)
                return 1;
        }  


        int factor = (int)d[0];
        for(int i = 0;i<d.length-1;i++){
            factor = gcd(factor,(int)d[i+1]);
        }


        return factor;
    }
}





