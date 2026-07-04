import javax.swing.*;

public class Main_phaseplane {

    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel page = new PhasePlane();
        window.add(page);

        window.setTitle("Physics Simulator");
        window.setResizable(false);
        window.pack();
        window.setVisible(true);
        window.setLocationRelativeTo(null);
    }

   
}