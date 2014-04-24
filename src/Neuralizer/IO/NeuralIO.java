package Neuralizer.IO;

import Compiler.Nodes.ASTNodeTypeJava7;
import Compiler.Parser.Builder.ASTBuilder;
import Compiler.Parser.CFG.ContextFreeGrammar;
import Compiler.Parser.LanguageSource.JavaGrammar;
import Compiler.Parser.ParserTree.ParserTreeNode;
import Compiler.Scanner.LexerToken;
import Compiler.Scanner.Scanner;
import Neuralizer.Network.NormalizeInput;
import Neuralizer.Network.SelfOrganizingMap;
import Neuralizer.Network.TrainSelfOrganizingMap;
import Neuralizer.Structure.Matrix;
import Neuralizer.Structure.NeuralizerTree;
import Neuralizer.Util.NeuralErrorWin;
import javafx.animation.AnimationTimer;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.stream.DoubleStream;

/**
 * Created by Matt Levine on 4/20/14.
 */
public class NeuralIO {

    private final String path = "C:\\Users\\Matt Levine\\Desktop\\Java Source";
    private final File directory;
    private ContextFreeGrammar grammar = JavaGrammar.getJavaGrammar();

    private final NeuralErrorWin errorWindow;
    private final SimpleBooleanProperty cflag = new SimpleBooleanProperty(true);
    private final SimpleBooleanProperty sflag = new SimpleBooleanProperty(false);
    private int step = 0;
    private long lastTime;
    private boolean stop = false;

    private final ArrayList<Matrix> trainingSet = new ArrayList<>();
    private final int NUM_ITERATIONS = 10;


    public NeuralIO(){
        errorWindow = new NeuralErrorWin("Error",400,550,cflag,sflag);
        report("Starting Neuralization");

        cflag.addListener((observableValue, aBoolean, aBoolean2) -> {
            if (aBoolean2) resume();
        });
        sflag.addListener((observableValue, aBoolean, aBoolean2)->{
            if (aBoolean2){
                SelfOrganizingMap map = null;
                TrainSelfOrganizingMap trainer = null;
                if (trainingSet.size() > 0) {

                    double[][] input = new double[trainingSet.get(0).size()][trainingSet.size()];
                    int nonNullSize = 0;

                    for (int i = 0; i < trainingSet.size(); i++){
                        if (DoubleStream.of(trainingSet.get(i).toPackedArray()).sum() != 0) {
                            input[i] = trainingSet.get(i).toPackedArray();
                            nonNullSize++;
                            System.out.println("G: "+DoubleStream.of(trainingSet.get(i).toPackedArray()).sum());
                        }
                    }

                    double[][] trainingInput = new double[nonNullSize][input[0].length];
                    System.arraycopy(input, 0, trainingInput, 0, nonNullSize);

                    map = new SelfOrganizingMap(nonNullSize, nonNullSize,
                            NormalizeInput.NormalizationType.MULTIPLICATIVE);

                    trainer = new TrainSelfOrganizingMap(map,trainingInput,
                            TrainSelfOrganizingMap.LearningMethod.ADDITIVE,0.5);
                    for (int i = 0; i < NUM_ITERATIONS; i++){
                        trainer.iteration();
                    }
                }
                stop = true;
                report("Scanning Stopped By User");
                System.out.println(map == null ? null : map.getOutputWeights());
            }
        });

        directory = new File(path);
        if (!directory.canRead() || !directory.isDirectory()){
            report("NeuralIO Instantiation Error (1" +
                    "): \n\tCannot " + "use NeuralIO without" + " local source directory");
            return;
        }


        new AnimationTimer(){

            @Override public void handle(long now) {
                if (stop) return;
                if ((now - lastTime)/0.5 >= 1000000000){
                    cflag.set(true);
                    lastTime = now;
                }
            }
        }.start();


        read(directory.listFiles());
    }


    private void read(File input){

        if (stop) return;
        report("Reading " + input.getName());
        String line;
        StringBuilder stringBuilder = null;
        if (input.isDirectory()) {
            read(input.listFiles());
        }
        else{
            stringBuilder = new StringBuilder();
            Charset charset = Charset.forName("US-ASCII");
            try{
                BufferedReader reader = Files.newBufferedReader(input.toPath(),charset);
                while((line = reader.readLine()) != null)
                    stringBuilder.append(line).append("\n");
            }catch (IOException ioe){
                report("Throwing IOException");
                report("NeuralIO Instantiation Error (2" +
                        "): \n\tCannot " + "use NeuralIO without" + " local source directory");
            }

            try {
                scanAndParse(stringBuilder, input.getName());
            } catch (InterruptedException e) {
                report("Err: Thread interruption in " + input.getName());
            }
        }
        cflag.set(false);
    }

    private Stack<File> unCheckedFiles = new Stack<>();

    private void read(File[] input){
        if (stop) return;
        report("Adding Directory of Size " + input.length);
        for ( File f : input ){
            if (cflag.get())
                read(f);
            else{
                unCheckedFiles.push(f);
            }
        }
    }

    private void resume(){
        if (stop) return;
        while(!unCheckedFiles.isEmpty()){
            if (cflag.get()){
                read(unCheckedFiles.pop());
            }else break;
        }
    }

    private void scanAndParse(StringBuilder stringBuilder,String filename) throws InterruptedException {
        if (stop) return;
        report("Scanning " + filename + " : (" + stringBuilder.length() + ")");
        Scanner scanner = new Scanner(stringBuilder.toString(),false);
        ArrayList<LexerToken> tokens = new ArrayList<>();
        LexerToken token;

        while ((token = scanner.getNextToken()).getIds() != LexerToken.TokenIds.EOF){
            tokens.add(token);
        }
        ASTBuilder builder = new ASTBuilder();

        report("Building (1) " + filename);
        Thread t1 = new Thread(()->{grammar.matches(
                tokens.toArray(new LexerToken[tokens.size()]), builder);
        });
        t1.start();
        long startTime = System.nanoTime();
        while (t1.isAlive()){
            if (System.nanoTime()-startTime > 5e9){
                report("Thread Err: Aborted " + filename);
                t1.interrupt();
                return;
            }
            Thread.sleep(100);
        }

        report("Matched " + filename);
        if (builder.getTreeHead() != null){
            report("Building (2) " + filename);


            Thread t2 = new Thread(()->{buildNeurlizerTree(builder.getTreeHead(),ASTNodeTypeJava7.class);});
            t2.start();
            t2.join();
            if (stop) return;

            NeuralizerTree neuralizerTree = lastTree;
            Matrix flat = neuralizerTree.flatten();

            report("Parsed: " + step);
            System.out.println(step);
            trainingSet.add(flat);
            step++;

            report(flat.toString());
        }
    }

    NeuralizerTree lastTree = null;

    private void buildNeurlizerTree(ParserTreeNode node, Class c){
        if (stop) return;
        lastTree = new NeuralizerTree(node,c);
    }

    private void report(final String nErr){
        errorWindow.mutate(nErr);
        errorWindow.show();
        errorWindow.centerOnScreen();
    }

}
