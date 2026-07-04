import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;


public class Matrix {
    
    int[][] mat;
    double[][] dMat;
    int rows;int cols;
    int a;int b;int c;int d;
    double da;double db;double dc;double dd;

    public Matrix(int r, int c, int[] n, double[] dou){
        rows = r; cols = c;
        int counter = 0;
        if(n == null){
            dMat = new double[rows][cols];
            for(int i=0;i<rows;i++){
                for(int j=0;j<cols;j++){
                    dMat[i][j] = dou[counter];
                    counter++;
                }
            }

            if(rows==2 && cols==1){
                da = dMat[0][0];dc=dMat[1][0];
            }

            if(rows==2 && cols==2){
                da=dMat[0][0];db=dMat[0][1];dc=dMat[1][0];dd=dMat[1][1];
            }
            mat = null;
        }
        else{
            mat = new int[rows][cols];
            for(int i=0;i<rows;i++){
                for(int j=0;j<cols;j++){
                    mat[i][j] = n[counter];
                    counter++;
                }
            }

            if(rows==2 && cols==1){
                a = mat[0][0];c=mat[1][0];
            }

            if(rows==2 && cols==2){
                a=mat[0][0];b=mat[0][1];c=mat[1][0];d=mat[1][1];
            }
            dMat = null;
        }
        

    }

    public void drawMatrix(Graphics2D g, int x, int y, int size, Color c){

        g.setColor(c);

        int[][] bra = {
            {x,x-10,x-10,x},
            {y,y,y+(size*rows*2),y+(size*rows*2)}
        };
        g.drawPolyline(bra[0],bra[1],4);

        int shift = (size*cols*2);
        bra[0] = new int[]{x+shift,x+shift+10,x+shift+10,x+shift};
        g.drawPolyline(bra[0],bra[1],4);

        g.setFont(new Font("Arial", Font.BOLD, 13-(13-size)/2));
        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                g.drawString(dMat==null?String.valueOf(mat[i][j]):String.valueOf(dMat[i][j]),x+5-(13-size)+(j*size*2),y+18-(13-size)+(i*size*2));
            }
        }

    }
    
    public int val(String position){
        if(position == "a" || position == "b")
            return mat[0][Character.getNumericValue(position.charAt(0))-Character.getNumericValue('a')];
        else if(position == "c" || position == "d")
            return mat[1][Character.getNumericValue(position.charAt(0))-Character.getNumericValue('c')];
        else{
            int a1 = Integer.parseInt(String.valueOf(position.charAt(0)));
            int b1 = Integer.parseInt(String.valueOf(position.charAt(1)));
            return mat[a1][b1];
        }
    }

    public double dVal(String position){
        if(position == "a" || position == "b")
            return dMat[0][Character.getNumericValue(position.charAt(0))-Character.getNumericValue('a')];
        else if(position == "c" || position == "d")
            return dMat[1][Character.getNumericValue(position.charAt(0))-Character.getNumericValue('c')];
        else{
            int a1 = Integer.parseInt(String.valueOf(position.charAt(0)));
            int b1 = Integer.parseInt(String.valueOf(position.charAt(1)));
            return dMat[a1][b1];
        }
    }

    public double[] eigenVector(double val){
        try{
            if(val == (int)val){
                int z = lcm((int)(a-val),b);
                return new double[]{z/(a-val),-1*z/b};
            }
        }
        catch(ArithmeticException e){System.out.println("Divide by zero error");}
        double x1 = (-1*b)/(a-val);
        return new double[]{x1,1};
    }

    public Matrix invMatrix(Matrix m){
        if(m.determinant() == 0)
            return null;
        else
            return new Matrix(m.rows,m.cols,null,new double[]{
                m.d/m.determinant(),
                -1*m.b/m.determinant(),
                -1*m.c/m.determinant(),
                m.a/m.determinant(),
            });
    }

    public Matrix dInvMatrix(Matrix m){
        if(m.dDeterminant() == 0)
            return null;
        else
            return new Matrix(m.rows,m.cols,null,new double[]{
                m.dd/m.dDeterminant(),
                -1*m.db/m.dDeterminant(),
                -1*m.dc/m.dDeterminant(),
                m.da/m.dDeterminant(),
            });
    }

    public Matrix mult(Matrix m){
        double[][] newMat = new double[rows][m.cols];
        for(int i=0;i<rows;i++){
            for(int k=0;k<m.cols;k++){
                for(int j=0;j<cols;j++){
                    newMat[i][k] += (mat==null?dMat[i][j]:mat[i][j]) * (m.mat==null?m.dMat[j][k]:m.mat[j][k]);
                    //System.out.println(newMat[i][k]+", "+i+","+k);
                }
            }
        }

        int count = 0;
        if(dMat==null && m.dMat==null){
            int[] n = new int[newMat.length*newMat[0].length];
            for(int i=0;i<newMat.length;i++){
                for(int j=0;j<newMat[0].length;j++){
                    n[count] = (int)newMat[i][j];
                    count++;
                }
            }
            return new Matrix(rows, m.cols, n, null);
        }
        else{
            double[] n = new double[newMat.length*newMat[0].length];
            for(int i=0;i<newMat.length;i++){
                for(int j=0;j<newMat[0].length;j++){
                    n[count] = newMat[i][j];
                    count++;
                }
            }
            return new Matrix(rows, m.cols, null, n);
        }
    }

    public void p(){
        System.out.println();
        System.out.print("[");
        for(int i=0;i<rows;i++){
            System.out.print(i>0?" ":"");
            for(int j=0;j<cols;j++){
                System.out.print((mat!=null?mat[i][j]:dMat[i][j])+(i==rows-1 && j==cols-1?"]":" "));
            }
            System.out.println();
        }
    }

    public int determinant(){return (mat[0][0]*mat[1][1])-(mat[0][1]*mat[1][0]);}
    public double dDeterminant(){return (dMat[0][0]*dMat[1][1])-(dMat[0][1]*dMat[1][0]);}

    private int lcm(int a, int b) { return a * (b / gcd(a, b)); }
    private int gcd(int a, int b) { return b==0 ? a : gcd(b, a%b); }


}