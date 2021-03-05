package com.ddf.fakeplayer.gui.dialog;

import com.ddf.fakeplayer.gui.GUIMain;
import com.ddf.fakeplayer.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AboutDialog extends JDialog {
    private final GUIMain main;
    private JButton okButton;
    private JLabel version;
    private JLabel mainPage;

    public AboutDialog(GUIMain main) {
        super(main.getFrame());
        this.main = main;
        initLayout();
        initData();
    }

    private void initLayout() {
        JPanel content = new JPanel();
        JPanel about = new JPanel();
        JLabel name = new JLabel("FakePlayer");
        version = new JLabel("版本 0.2.0-SNAPSHOT");
        mainPage = new JLabel("<html><a href='https://github.com/ddf8196/FakePlayer'>项目主页</a></html>");
        JPanel spacer = new JPanel();
        JPanel buttonBar = new JPanel();
        okButton = new JButton("确定");

        content.setBorder(new EmptyBorder(12, 12, 12, 12));
        content.setLayout(new BorderLayout());
        setContentPane(content);
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {0, 0};
        gridBagLayout.rowHeights = new int[] {0, 0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[] {0.0, 0.0001};
        gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0001};
        about.setLayout(gridBagLayout);
        about.add(name, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 5, 0), 0, 0));
        about.add(version, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 5, 0), 0, 0));
        about.add(mainPage, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                new Insets(0, 0, 5, 0), 0, 0));
        spacer.setLayout(new BorderLayout());
        about.add(spacer, new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        content.add(about, BorderLayout.CENTER);
        buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
        gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {80};
        buttonBar.setLayout(gridBagLayout);
        buttonBar.add(okButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        content.add(buttonBar, BorderLayout.SOUTH);
        setTitle("关于");
        pack();
        setLocationRelativeTo(getOwner());
    }

    private void initData() {
        mainPage.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    Util.tryOpenBrowser("https://github.com/ddf8196/FakePlayer");
                }
            }
        });
        okButton.addActionListener(e -> dispose());
    }
}
