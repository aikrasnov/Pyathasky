import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

/**
 * Created by akrasnov on 04.12.2016.
 */
public class SliderPuzzle {
    private JFrame mainFrame = new JFrame();
    private JPanel slider = new JPanel();
    private JPanel controlPanel = new JPanel();
    private JLabel status = new JLabel("Тут будет отображаться всякое.", SwingConstants.CENTER);
    private JButton resetBtn = new JButton("Сбросить");
    private JButton mixBtn = new JButton("Перемешать");
    private JButton acceptDifficult = new JButton("Принять изменение");
    private JSlider difficultSlider = new JSlider(0, 3, 0);
    private int rows_default = 3;
    private int rows = rows_default;
    private int cols = rows;
    private JButton[][] cells = new JButton[rows][cols];
    private static SliderPuzzle puzzle = new SliderPuzzle();

    public static void main(String[] args) {
        puzzle.startUI();
    }

    private void refreshComponents() {
        mainFrame.remove(slider);
        cells = new JButton[rows][cols];
        slider = new JPanel();
        createGrid();
        mainFrame.add(slider);

    }

    private synchronized void switchControlPanel(boolean state){
        Component[] components = controlPanel.getComponents();
        for (Component item:components) {
            item.setEnabled(state);
            acceptDifficult.setEnabled(state);
            difficultSlider.setEnabled(state);
        }
    }

    private void refreshGrid() {
        for (Integer i = 0; i < rows; i++) {
            for (Integer j = 0; j < cols; j++) {
                cells[i][j].setActionCommand(i.toString() + ";" + j.toString());
                slider.add(cells[i][j]);
            }
        }
        slider.updateUI();
    }

    private void resetCells() {
        slider.removeAll();
        createGrid();
    }

    private void mixCells() {
        //лучший рандом в мире.
        switchControlPanel(false);
        Random rand = new Random();
        for (int s = 0; s < 100; s++) {
            int t = rand.nextInt(4) + 1;
            for (Integer i = 0; i < rows; i++) {
                for (Integer j = 0; j < cols; j++) {
                    if (!cells[i][j].isVisible()) {
                        if (i-1>=0&&t==1) cells[i-1][j].doClick();
                        if (i+1<rows&&t==2) cells[i+1][j].doClick();
                        if (j-1>=0&&t==3) cells[i][j-1].doClick();
                        if (j+1<cols&&t==4) cells[i][j+1].doClick();
                        }
                    }
                }
            }
        switchControlPanel(true);
    }

    private void checkWin() {
        String[] textCells = new String[rows * cols];
        String[] winSequence = new String[rows * cols];
        Integer count = 0;
        for (Integer i = 0; i < rows; i++) {
            for (Integer j = 0; j < cols; j++) {
                textCells[count] = cells[i][j].getText();
                count++;
                winSequence[count - 1] = count.toString();
            }
        }
        for (int i = 0; i < textCells.length; i++) {
            if (!textCells[i].equals(winSequence[i])) {
                status.setText("Рано праздновать...");
                return;
            }
        }
        status.setText("Все стоит правильно!");
    }

    private void moveCell(String oldPosition) {
        String coordinates[] = oldPosition.split(";");
        int row = Integer.parseInt(coordinates[0]);
        int col = Integer.parseInt(coordinates[1]);
        JButton temp = cells[row][col];
        try {
            if (!cells[row + 1][col].isVisible()) {
                cells[row][col] = cells[row + 1][col];
                cells[row + 1][col] = temp;
                refreshGrid();
                return;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            if (!cells[row - 1][col].isVisible()) {
                cells[row][col] = cells[row - 1][col];
                cells[row - 1][col] = temp;
                refreshGrid();
                return;

            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            if (!cells[row][col - 1].isVisible()) {
                cells[row][col] = cells[row][col - 1];
                cells[row][col - 1] = temp;
                refreshGrid();
                return;

            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
        try {
            if (!cells[row][col + 1].isVisible()) {
                cells[row][col] = cells[row][col + 1];
                cells[row][col + 1] = temp;
                refreshGrid();
                return;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
        }
    }

    private void allocateMemoryForGrid() {
        cells = new JButton[rows][cols];
        slider = new JPanel();
        slider.updateUI();
    }

    private void createGrid() {
        slider.setLayout(new GridLayout(rows, cols, 2, 2));
        int count = 1;

        for (Integer i = 0; i < rows; i++) {
            for (Integer j = 0; j < cols; j++) {
                JButton cell = new JButton(String.valueOf(count));
                cell.setActionCommand(i.toString() + ";" + j.toString());
                cell.addActionListener(new RefreshCells());
                if (count + 1 <= rows * cols) {
                    slider.add(cell);
                } else {
                    cell.setVisible(false);
                    slider.add(cell);
                }
                cells[i][j] = cell;
                count++;
            }
        }
        slider.updateUI();
    }

    private void startUI() {
        mainFrame.setSize(400, 800);
        mainFrame.setResizable(false);
        mainFrame.setLayout(new GridLayout(2, 2));
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        allocateMemoryForGrid();
        createGrid();

        controlPanel.setLayout(new GridLayout(4, 3));
        resetBtn.addActionListener(new ResetCells());

        mixBtn.addActionListener(new MixCells());
        mixBtn.setToolTipText("Рандомизатор работает плохо, так что перемешивайте несколько раз.");

        JPanel difficult = new JPanel();
        difficult.setLayout(new BorderLayout());

        difficultSlider.addChangeListener(new ChangeDifficult());
        acceptDifficult.addActionListener(new AcceptChange());

        difficult.add(acceptDifficult, BorderLayout.LINE_END);
        difficult.add(new JLabel("Сложность: "), BorderLayout.LINE_START);
        difficult.add(difficultSlider, BorderLayout.CENTER);

        controlPanel.add(status);
        controlPanel.add(resetBtn);
        controlPanel.add(mixBtn);
        controlPanel.add(difficult);

        mainFrame.add(controlPanel);
        mainFrame.add(slider);
        mainFrame.setVisible(true);

    }

    class RefreshCells implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            status.setText("Клик на ячейку " + e.getActionCommand());
            moveCell(e.getActionCommand());
            checkWin();
        }
    }

    class ResetCells implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            status.setText("Приводим в порядок поле...");
            resetCells();
            status.setText("Готово! Поле сброшено.");
        }
    }

    class MixCells extends Thread implements ActionListener {
        public void run() {
            mixCells();
        }
        public void start() {
            new Thread(this).start();
        }
        public void actionPerformed(ActionEvent e) {
            status.setText("Перемешиваем ячейки...");
            MixCells T1 = new MixCells();
            T1.start();
            status.setText("Готово! Ячейки перемешаны.");
        }
    }

    class AcceptChange implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            int n = difficultSlider.getValue();
            status.setText("Изменяем сложность на +" + n);
            if (n == 0) {
                rows = rows_default;
                cols = rows_default;
            } else {
                rows = rows_default + n;
                cols = rows;
            }
            refreshComponents();
            status.setText("Готово! Cложность изменена.");
        }
    }
    class ChangeDifficult implements ChangeListener{
        public void stateChanged(ChangeEvent e) {
            status.setText("Сложность будет изменена на: " + difficultSlider.getValue());

            }
        }
}




