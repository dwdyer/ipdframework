// $Header: $
package au.edu.uwa.csse.dyerd01.ipd.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class ProgressDialog extends JDialog
{
    private JProgressBar progressBar;
    private JLabel statusLabel = new JLabel("Evaluating generation 0...   ");
    private int value = 0;
    
    public ProgressDialog(Frame owner, int noOfIncrements)
    {
        super(owner, "Processing...", true);
        getContentPane().setLayout(new BorderLayout());
        progressBar = new JProgressBar(0, noOfIncrements);
        getContentPane().add(progressBar, BorderLayout.CENTER);
        getContentPane().add(statusLabel, BorderLayout.SOUTH);
        pack();
        setResizable(false);
    }
    
    
    public void increment()
    {
        value++;
        progressBar.setValue(value);
        statusLabel.setText("Evaluating generation " + value + "...");
        if (value >= progressBar.getMaximum())
        {
            hide();
            dispose();
        }
    }
}