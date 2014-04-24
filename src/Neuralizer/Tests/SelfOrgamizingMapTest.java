package Neuralizer.Tests;

import Neuralizer.Structure.Matrix;
import Neuralizer.Network.NormalizeInput;
import Neuralizer.Network.SelfOrganizingMap;
import Neuralizer.Network.TrainSelfOrganizingMap;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;

/**
 * This class provides an extremely simple visualization
 * of the SOM. Full documentatino not provided. This is directly
 * copied from Heaton.
 * Created by Matt Levine on 4/14/14.
 */
public class SelfOrgamizingMapTest extends JFrame implements Runnable {

    public static final int INPUT_COUNT = 2;
    public static final int OUTPUT_COUNT = 7;
    public static final int SAMPLE_COUNT = 100;


    public static void main(String[] args){
        final SelfOrgamizingMapTest app = new SelfOrgamizingMapTest();
        app.setVisible(true);
        final Thread t = new Thread(app);
        t.setPriority(Thread.MIN_PRIORITY);
        t.start();
    }

    protected int unitLength;
    protected int retry = 1;
    protected double totalError = 0;
    protected double bestError = 0;
    protected SelfOrganizingMap net;
    protected double input[][];
    protected Image offScreen;

    SelfOrgamizingMapTest(){
        setTitle("Test");
        setSize(400,450);
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension d = toolkit.getScreenSize();
        setLocation((int) (d.width - getSize().getWidth()) / 2,
                (int) (d.height - this.getSize().getHeight()) / 2);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
    }

    @Override
    public void paint(Graphics g){
        if (net == null)return;
        if (offScreen==null){
            offScreen=createImage((int)getBounds().getWidth(),
                    (int)getBounds().getHeight());
        }
        g = offScreen.getGraphics();
        int width = getContentPane().getWidth();
        int height = getContentPane().getHeight();
        unitLength = Math.min(width,height);
        g.setColor(Color.BLACK);
        g.fillRect(0,0,width,height);

        //plot weight
        g.setColor(Color.WHITE);
        Matrix outputWeights = net.getOutputWeights();
        for (int y = 0; y < outputWeights.getRows(); y++){
            g.fillRect((int)(outputWeights.get(y,0)*unitLength),
                    (int)(outputWeights.get(y,1) * unitLength),
                    10,10);
        }

        g.setColor(Color.GREEN);
        for (int y = 0; y  < unitLength; y += 50){
            for (int x =0 ; x < unitLength; x += 50){
                g.fillOval(x,y,5,5);
                final double d[] = new double[2];
                d[0] = x;
                d[1] = y;
                final int c = net.winner(d);

                int x2 = (int) (outputWeights.get(c,0) * unitLength);
                int y2 = (int) (outputWeights.get(c,1) * unitLength);
                g.drawLine(x,y,x2,y2);
            }
        }

        g.setColor(Color.WHITE);
        NumberFormat nf = NumberFormat.getCurrencyInstance().getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        g.drawString("retry = "+this.retry + ", current error = "+nf.format(totalError*100) +
                        "%, best error = "+nf.format(bestError*100) +
                        "%",0,(int) getContentPane().getBounds().getHeight());
        getContentPane().getGraphics().drawImage(offScreen,0,0,this);
    }

    @Override
    public void run() {
        input = new double[SAMPLE_COUNT][INPUT_COUNT];

        for (int i = 0; i < SAMPLE_COUNT; i++){
            for (int j = 0; j < INPUT_COUNT; j++){
                input[i][j] = Math.random();
            }
        }

        net = new SelfOrganizingMap(INPUT_COUNT,OUTPUT_COUNT,
                NormalizeInput.NormalizationType.MULTIPLICATIVE);
        TrainSelfOrganizingMap train = new TrainSelfOrganizingMap(net,input,
                TrainSelfOrganizingMap.LearningMethod.SUBTRACTIVE,0.5);
        train.initialize();
        double lastError = Double.MAX_VALUE;
        int errorCount = 0;

        while (errorCount < 10){
            train.initialize();
            retry++;
            totalError = train.getTotalError();
            bestError = train.getBestError();
            paint(getGraphics());

            if (bestError < lastError){
                lastError = bestError;
                errorCount = 0;
            }else{
                errorCount++;
            }
        }
    }
}
