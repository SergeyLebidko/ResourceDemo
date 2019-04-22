package resourcedemo;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedList;

public class ResourceDemo {

    private final int w = 1300;
    private final int h = 800;

    private JFrame frm;

    private DataBaseConnector dataBaseConnector;

    private JTextArea sqlQueryArea;
    private JTextArea resultArea;
    private JButton executeQueryBtn;
    private JButton clearBtn;

    public ResourceDemo() {
//        String laf = UIManager.getSystemLookAndFeelClassName();
//        try {
//            UIManager.setLookAndFeel(laf);
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
//            JOptionPane.showMessageDialog(null, "Возникла ошибка при попытке переключить стиль интерфейса. Работа программы будет прекращена", "Ошибка", JOptionPane.ERROR_MESSAGE);
//            System.exit(0);
//        }
        frm = new JFrame("Главное окно");

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("images/logo.png").getFile());

        Image logoImage = null;
        Image executeImage = null;
        Image clearImage = null;
        try {
            logoImage = ImageIO.read(classLoader.getResourceAsStream("images/logo.png"));
            executeImage = ImageIO.read(classLoader.getResourceAsStream("images/execute.png"));
            clearImage = ImageIO.read(classLoader.getResourceAsStream("images/clear.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Не могу прочитать файл ресурса. Ошибка: "+e.getMessage(), "Ошибка доступа к ресурсу", JOptionPane.ERROR_MESSAGE);
        }
        if (logoImage!=null)frm.setIconImage(logoImage);

        try {
            dataBaseConnector = new DataBaseConnector();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Возникла ошибка при подключении к БД: "+e.getMessage(), "Ошибка подключения к БД", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setSize(w, h);
        int xPos = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - w / 2;
        int yPos = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - h / 2;
        frm.setLocation(xPos, yPos);

        JPanel contentPane = new JPanel();
        {
            //Формируем поле ввода запроса и кнопку "Выполнить запрос"
            contentPane.setLayout(new BorderLayout(5, 5));
            contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            sqlQueryArea = new JTextArea();
            executeQueryBtn = new JButton("Выполнить запрос");
            if (executeImage!=null)executeQueryBtn.setIcon(new ImageIcon(executeImage));
            sqlQueryArea.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            clearBtn = new JButton("Очистить");
            if (clearImage!=null)clearBtn.setIcon(new ImageIcon(clearImage));
            JPanel topPane = new JPanel();
            topPane.setLayout(new BorderLayout(5, 5));
            Box box1 = Box.createHorizontalBox();
            box1.add(sqlQueryArea);
            box1.add(Box.createHorizontalStrut(5));
            Box box2 = Box.createVerticalBox();
            box2.add(executeQueryBtn);
            box2.add(Box.createVerticalStrut(5));
            box2.add(clearBtn);
            box2.add(Box.createVerticalGlue());
            box1.add(box2);
            topPane.add(box1, BorderLayout.SOUTH);
            contentPane.add(topPane, BorderLayout.NORTH);

            //Формируем область вывода результатов
            resultArea = new JTextArea();
            resultArea.setEditable(false);
            resultArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        }

        executeQueryBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String query = sqlQueryArea.getText();
                LinkedList<String> resultList = new LinkedList<>();
                try {
                    ResultSet resultSet = dataBaseConnector.getResultSet(query);
                    resultList = getResultText(resultSet);
                } catch (Exception ex) {
                    resultList.add("Ошибка при выполнении запроса: "+ex.getMessage());
                }
                showResult(resultList);
            }
        });

        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultArea.setText("");
            }
        });

        frm.add(contentPane, BorderLayout.NORTH);
        frm.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        frm.setVisible(true);
    }

    private LinkedList<String> getResultText(ResultSet resultSet){
        LinkedList<String> res = new LinkedList<>();

        if (resultSet==null){
            res.add("ResultSet is null...");
            return res;
        }

        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            String headline="";
            String headlineBottom="";

            for (int i=1;i<=metaData.getColumnCount();i++){
                headline+=String.format("%20s",metaData.getColumnName(i));
                headlineBottom+="    ----------------";
            }
            res.add(headline);
            res.add(headlineBottom);

            String resultStr;
            Object cellStr;
            while (resultSet.next()){
                resultStr="";
                for (int i=1;i<=metaData.getColumnCount();i++){
                    cellStr=resultSet.getObject(i);
                    if (cellStr==null){
                        cellStr="null";
                    }else {
                        cellStr=cellStr.toString();
                    }
                    resultStr+=String.format("%20s", cellStr);
                }
                res.add(resultStr);
            }

        } catch (SQLException e) {
            res.add(e.getMessage());
        }

        return res;
    }

    private void showResult(LinkedList<String> resultList){
        for (String s: resultList){
            resultArea.append(s+"\n");
        }
        resultArea.append("\n");
        resultArea.append("\n");
    }

    private static void startSwing() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ResourceDemo();
            }
        });
    }

    public static void main(String[] args) {
        startSwing();
    }


}
