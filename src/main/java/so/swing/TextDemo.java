package so.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TextDemo extends JFrame implements FocusListener {
    JTextArea jTextArea;
    JScrollPane jCrollPane;
    JButton nextButton;
    ArrayList<String> arrayListPL = new ArrayList<String>();
    ArrayList<String> arrayListRU = new ArrayList<String>();
    //x,y used for getting specific String from the ArrayLists
    int x = 0;
    int y = 0;

    public static void main(String[] args) throws IOException {
        TextDemo mainRunner = new TextDemo();
        mainRunner.RepeatMethod();
    }
    public void RepeatMethod() throws IOException {
        // System.out.println(System.getProperty("file.encoding"));
        // System.setProperty("file.encoding", "UTF-8");
        Repeat();
        TextDemo runner = new TextDemo();
        runner.Repeat();
        runner.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        runner.CreateWindow();
    }

    private void CreateWindow() {

        setSize(400, 600);
        setTitle("Powtórka");
        setResizable(false);
        setVisible(true);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2,
                dim.height / 2 - this.getSize().height / 2);
        jTextArea = new JTextArea();
        jCrollPane = new JScrollPane(jTextArea);
        jCrollPane.setBounds(0, 0, 400, 500);
        jTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
        add(jCrollPane);
        nextButton = new JButton("Next word");
        nextButton.setBounds(0, 500, 400, 100);
        nextButton.setSize(400, 100);
        add(nextButton, BorderLayout.PAGE_END);
        jTextArea.addFocusListener(this);

    }

    public void Repeat() throws IOException {
        File plik = new File("src/main/java/so/swing/file.txt");
        FileReader fr = new FileReader(plik);
        BufferedReader br = new BufferedReader(fr);
        String line;
        String[] split;
        String wordRU, wordPL;
        while ((line = br.readLine()) != null) {
            // Dividing the pair of words
            split = line.split("-");
            wordRU = split[0].trim();
            wordPL = split[1].trim();
//            wordRU = new String(wordRU.getBytes("Cp1250"), "UTF-8");
//            wordPL = new String(wordPL.getBytes("Cp1250"), "UTF-8");
            arrayListPL.add((wordPL));
            arrayListRU.add((wordRU));

        }

        br.close();

    }

    @Override
    public void focusGained(FocusEvent arg0) {

        jTextArea.setText(jTextArea.getText() + "\n" +
                arrayListPL.get(x));
        if (x < arrayListPL.size()) {
            x++;
        }
    }

    @Override
    public void focusLost(FocusEvent arg0) {
        jTextArea.setText(jTextArea.getText() + "\n" +
                arrayListRU.get(y));
        if (y < arrayListRU.size()) {
            y++;
        }
    }

}