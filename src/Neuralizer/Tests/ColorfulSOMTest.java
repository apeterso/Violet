package Neuralizer.Tests;

import Neuralizer.Network.NormalizeInput;
import Neuralizer.Network.SelfOrganizingMap;
import Neuralizer.Network.TrainSelfOrganizingMap;
import Neuralizer.Network.TrainSelfOrganizingMap.LearningMethod;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * This test demonstrates the visualization and clustering ability of a Self Organizing
 * Map (SOM). A grid of random colors is generated and shown; through several iterations,
 * the SOM attempts to classify the colors based on a trained input palette.
 * Created by Matt Levine on 4/16/14.
 */
public final class ColorfulSOMTest extends JFrame implements Runnable, KeyEventDispatcher{

    private int retry = 1;
    private SelfOrganizingMap som;
    private final int width = 280;
    private final int height = 200;
    private final int randomDim = 100000;
    private final double[][] randomMtx = new double[randomDim][3];
    protected BufferedImage offScreen;
    private int step;

    /** Runs a simulation that clusters random colors using a SOM **/
    public static void main(String[] args){
        final ColorfulSOMTest app = new ColorfulSOMTest();
        app.setVisible(true);
        final Thread t = new Thread(app);
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    /** Generates a new ColorfulSOMTest **/
    public ColorfulSOMTest(){
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(this);
        genRandomMtx();
        initGUI();
    }

    /** Randomizes the internal 'matrix' test database **/
    private void genRandomMtx(){
        for (int index = 0; index < randomDim; index++){
            for (int bin = 0; bin < 3; bin++){
                randomMtx[index][bin] = Math.random();
            }
        }
    }

    /**
     * Runs the simulation.
     */
    @Override public void run(){
        som = new SelfOrganizingMap(3,16,
                NormalizeInput.NormalizationType.MULTIPLICATIVE);

        double[][] patterns = {{1,0,0},{0,1,0},{0,0,1},
                {1,1,0},{1,0,1},{0,1,1},{0.01,0.01,0.01},{1,1,1},
                {0.5,0.5,0.5},{0.25,0.25,0.25},{0,0.5,1},{0,1,0.5},
                {0.5,1,0},{0.5,0,1},{1,0,0.5},{1,.5,0}};


        TrainSelfOrganizingMap trainer = new TrainSelfOrganizingMap(som,patterns,
                LearningMethod.ADDITIVE,0.5);
        trainer.initialize(); //fixme: should this be automatically called? If not, make invariant
        paint(getGraphics());
        System.out.println("Initialized");
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        double lastError = Double.MAX_VALUE;

        int errorCount = 0;

        while (errorCount < 1000){
            step++;
            retry++;
            double bestError = trainer.getBestError();
            paint(getGraphics());
            trainer.iteration();

            if (bestError < lastError){
                lastError = bestError;
                errorCount = 0;
            }else errorCount++;
            setTitle("Step "+step);
        }
        paint(getGraphics());

    }

    /** Initializes GUI products **/
    private void initGUI(){
        setSize(width, height);
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension d = toolkit.getScreenSize();
        setLocation((int) (d.width - this.getSize().getWidth()) / 2,
                (int) (d.height - this.getSize().getHeight()) / 2);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    /** Draws onto the frame
     * @param g a graphics object
     */
    @Override public void paint(Graphics g){
        if (som == null) {
            return;
        }
        if (offScreen == null) {
            offScreen = new BufferedImage((int) getBounds().getWidth(),
                    (int) getBounds().getHeight(),BufferedImage.TYPE_INT_RGB);

            visualizeRandom();
        }else{
            visualizeClustered();
        }

    }

    /** Subroutine for visualizing the random data **/
    private void visualizeRandom(){
        System.out.println("there");
        Graphics g = offScreen.getGraphics();

        offScreen.getGraphics().clearRect(0,0,width,height);
        g.setColor(Color.BLACK);
        offScreen.getGraphics().drawRect(0,0,width,height);

        for (int i = 0; i< randomDim; i++){
            final int columnIndex = (int) (Math.random()*7);
            final int x = (columnIndex % 7)*40+(int)(Math.random()*40);
            final int y = (int)(Math.random()*height);

            int bin1 = (int) (randomMtx[i][0] * 255);
            int bin2 = (int) (randomMtx[i][1] * 255);
            int bin3 = (int) (randomMtx[i][2] * 255);
            g.setColor(new Color(bin1,bin2,bin3));
            g.fillOval(x,y,5,5);
        }

        getContentPane().getGraphics().drawImage(offScreen, 0, 0, this);
        try {
            ImageIO.write(offScreen,"PNG",new File("otuput_"+step+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Subroutine for visualizing the data when clustered **/
    private void visualizeClustered(){
        Graphics g = offScreen.getGraphics();

        offScreen.getGraphics().clearRect(0,0,width,height);
        g.setColor(Color.BLACK);
        offScreen.getGraphics().fillRect(0,0,width,height);
        HashSet<Integer> setTest = new HashSet<>();


        for (int i = 0; i< randomDim; i++){
            final int columnIndex = som.winner(randomMtx[i]);
            setTest.add(columnIndex);
            final double rad = columnIndex*8+(Math.random()*8);
            final double theta = (Math.random()*2*Math.PI);
            final int x = width/2+(int) (rad * Math.cos(theta));
            final int y = height/2+(int)(rad * Math.sin(theta));

            int bin1 = (int) (randomMtx[i][0] * 255);
            int bin2 = (int) (randomMtx[i][1] * 255);
            int bin3 = (int) (randomMtx[i][2] * 255);
            g.setColor(new Color(bin1,bin2,bin3));
            g.fillOval(x,y,5,5);
            if (i % 50 == 0) System.out.println("Completed "+i*1.0/randomDim*100+"%");
        }

        List out = Arrays.asList(setTest.toArray());
        Collections.sort(out);

        getContentPane().getGraphics().drawImage(offScreen,0,0,this);
        try {
            ImageIO.write(offScreen,"PNG",new File("output_"+step+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** On key press 'r', redo simulation
     * @param e the key event to check if 'r'
     * @return true if refreshed
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyChar() == 'r'){
            genRandomMtx();
            visualizeClustered();
            return true;
        }
        return false;
    }
}
