package com.lizhongbin.ch_final;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, GestureDetector.OnGestureListener {
    private GestureDetectorCompat detector;
    private int[][] cellArr;
    private DBHelper dbHelper;
    private Record record;
    private TextView scoreView;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //附加监听器
        detector = new GestureDetectorCompat(this, this);
        dbHelper = new DBHelper(this);
        recyclerView = findViewById(R.id.recycle_view);
        scoreView = findViewById(R.id.yourScore_text);

        this.findViewById(R.id.mainLayout).setOnTouchListener(this);
        this.findViewById(R.id.restartButton).setOnClickListener(view -> reStartGame());
        //初始化游戏状态
        startGame();
    }

    private void gameInitialize() {
        cellArr = new int[][]{{0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}, {0, 0, 0, 0}};
        int startNum = 2;
        record = new Record(0, "Li");

        updateScoreView(record.getScores());
        //更新你的分数
        for (int i = 0; i < startNum; i++) {
            fillOneEmptyCell();
        }
        showCells();
    }

    private void reStartGame() {
        updateRank();
        gameInitialize();
    }

    private void startGame() {
        RecordAdapter adapter = new RecordAdapter(queryData());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        gameInitialize();
    }

    private void fillOneEmptyCell() {
        int emptyCellPosition = findOneEmptyCell();
        if (emptyCellPosition != -1) {
            cellArr[emptyCellPosition / 4][emptyCellPosition % 4] = getRandomValue();
        }
    }

    private int findOneEmptyCell() {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (cellArr[i][j] == 0) {
                    list.add(i * 4 + j);
                }
            }
        }
        int position = -1;
        if (!list.isEmpty()) {
            position = list.get(random(list.size()));
        }
        return position;
    }

    /**
     * 生成新的数字，90%几率生成2，10%几率生成4
     */
    private int getRandomValue() {
        int rand = random(10);
        int value = 2;
        if (rand >= 9) {
            value = 4;
        }
        return value;
    }

    /**
     * 生成随机数，0=<结果<max
     */
    private int random(int max) {
        return new SecureRandom().nextInt(max);
    }

    /**
     * 判断是否还可以移动。
     * 1、当前单元格是否为0；
     * 2、当前单元格和右侧单元格是否相等；
     * 3、当前单元格和下方单元格是否相等。
     */
    private boolean canMove() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (cellArr[j][i] == 0) {
                    return true;
                }
                if (j < 3 && cellArr[i][j] == cellArr[i][j + 1]) {//和右侧单元格比较，是否相等
                    return true;
                }
                if (i < 3 && cellArr[i][j] == cellArr[i + 1][j]) {//和下方单元格比较，是否相等
                    return true;
                }
            }
        }

        return false;
    }

    //积分功能
    private int countScore() {
        int s = 0;
        for (int i = 0; i < cellArr.length; i++)
            for (int j = 0; j < cellArr[i].length; j++) {
                s += cellArr[i][j];
            }
        return s;
    }

    private void setScore(int score) {
        record.setScores(score);
    }

    private void updateScoreView(int score) {
        String string = "你的分数是" + score;
        scoreView.setText(string);
    }

    private void showCells() {
        int[] textViewIds = new int[]{
                R.id.textView0, R.id.textView1, R.id.textView2, R.id.textView3,
                R.id.textView4, R.id.textView5, R.id.textView6, R.id.textView7,
                R.id.textView8, R.id.textView9, R.id.textView10, R.id.textView11,
                R.id.textView12, R.id.textView13, R.id.textView14, R.id.textView15
        };

        // 循环遍历二维数组和文本视图ID
        for (int i = 0; i < cellArr.length; i++) {
            for (int j = 0; j < cellArr[i].length; j++) {
                // 根据i和j计算出对应的textViewId
                int textViewId = textViewIds[i * cellArr[i].length + j];

                // 调用showCell方法
                showCell(textViewId, cellArr[i][j]);
            }
        }
    }

    private void showCell(int textViewResId, int cellValue) {
        TextView textView = this.findViewById(textViewResId);
        if (cellValue == 0) {
            textView.setText("");
        } else {
            textView.setText(String.valueOf(cellValue));
        }
        textView.setBackgroundColor(Color.parseColor(getCellBackgroundColor(cellValue)));
    }

    /**
     * 将单元格数向左或向右移动，移除零并对相邻相同数进行叠加
     *
     * @param toLeft 表示是否是向左
     */
    private void horizontalMoveCells(boolean toLeft) {
        for (int i = 0; i < 4; i++) {
            int[] newArr = new int[4];

            System.arraycopy(cellArr[i], 0, newArr, 0, 4);

            int[] resultArr = removeZerosAndAdd(newArr, toLeft);
            System.arraycopy(resultArr, 0, cellArr[i], 0, 4);
        }
    }

    /**
     * 将单元格数向下或向上移动，移除零并对相邻相同数进行叠加
     *
     * @param toTop 表示是否是向上
     */
    private void verticalMoveCells(boolean toTop) {
        for (int i = 0; i < 4; i++) {
            int[] newArr = new int[4];

            for (int j = 0; j < 4; j++) {
                newArr[j] = cellArr[j][i];
            }

            int[] resultArr = removeZerosAndAdd(newArr, toTop);
            for (int j = 0; j < 4; j++) {
                cellArr[j][i] = resultArr[j];
            }
        }
    }


    /**
     * 1、去掉数组中的零，向头或向尾压缩数组。
     * 0,4,0,4向左压缩变成：4,4,0,0. 向右压缩变成：0,0,4,4
     * 2、相邻的数如果相同，则进行相加运算。
     * 4,4,0,0向左叠加变成：8,0,0,0. 向右叠加变成：0,0,0,8
     * toHead表示是否是头压缩
     */
    private int[] removeZerosAndAdd(int[] arr, boolean toHead) {
        int[] newArr = new int[4];
        List<Integer> arrWithoutZero = new ArrayList<>();
        for (int j : arr) {
            if (j != 0) {
                arrWithoutZero.add(j);
            }
        }

        if (arrWithoutZero.isEmpty()) return newArr;
        if (toHead) {
            for (int i = 0; i < arrWithoutZero.size(); i++) {
                newArr[i] = arrWithoutZero.get(i);
            }

            //对相邻相同的数进行叠加
            for (int i = 0; i < newArr.length - 1; i++) {
                if (newArr[3 - i] == newArr[2 - i] && newArr[3 - i] != 0) {
                    newArr[3 - i] = 0;
                    newArr[2 - i] *= 2;
                }
            }
        } else {
            for (int i = 0; i < arrWithoutZero.size(); i++) {
                newArr[newArr.length - i - 1] = arrWithoutZero.get(arrWithoutZero.size() - i - 1);
            }

            //对相邻相同的数进行叠加
            for (int i = 0; i < newArr.length - 1; i++) {
                if (newArr[i] == newArr[i + 1] && newArr[i] != 0) {
                    newArr[i] = 0;
                    newArr[i + 1] *= 2;
                }
            }
        }

        return newArr;
    }


    private String getCellBackgroundColor(int cellValue) {
        switch (cellValue) {
            case 2:
                return "#EEE4DA";
            case 4:
                return "#EDE0C8";
            case 8:
                return "#F26179";
            case 16:
                return "#F59563";
            case 32:
                return "#F67C5F";
            case 64:
                return "#F65E36";
            case 128:
                return "#EDCF72";
            case 256:
                return "#EDCC61";
            case 512:
            case 2048:
                return "#90C000";
            case 1024:
                return "#3365A5";
            case 4096:
                return "#60B0C0";
            case 8192:
                return "#9030C0";
            default:
                return "#CDC1B4";
        }
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        assert e1 != null;
        if (e1.getY() - e2.getY() > (float) 100) {
            this.moveUp();
        } else if (e2.getY() - e1.getY() > (float) 100) {
            this.moveDown();
        } else if (e1.getX() - e2.getX() > (float) 100) {
            this.moveLeft();
        } else if (e2.getX() - e1.getX() > (float) 100) {
            this.moveRight();
        }
        return true;
    }

    private void moveUp() {
        verticalMoveCells(true);
        checkGameOverOrContinue();
    }

    private void moveDown() {
        verticalMoveCells(false);
        checkGameOverOrContinue();
    }

    private void moveLeft() {
        horizontalMoveCells(true);
        checkGameOverOrContinue();
    }

    private void moveRight() {
        horizontalMoveCells(false);
        checkGameOverOrContinue();
    }

    private void checkGameOverOrContinue() {
        if (canMove()) {
            //更新你的分数，更新本地数据并显示
            fillOneEmptyCell();
            showCells();
            setScore(countScore());
            updateScoreView(record.getScores());

            Log.d("score", "现在得分为" + record.getScores());
        } else {
            setScore(countScore());
            if (countScore() == record.getScores()) {
                Toast.makeText((Context) this, (CharSequence) "游戏结束", Toast.LENGTH_LONG).show();
            } else {
                updateScoreView(record.getScores());
                Log.d("score", "最终得分为" + record.getScores());
                //更新排行榜,上传数据库并更新显示
                updateRank();
            }
        }
    }

    //更新排行榜
    public void updateRank() {
        insertData();
        //由适配器进行更新排行榜
        RecordAdapter adapter = new RecordAdapter(queryData());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void insertData() {
        dbHelper.addRecord(record);
    }

    public List<Record> queryData() {
        return dbHelper.getAllRecords();
    }

    public boolean onTouch(View v, MotionEvent event) {
        detector.onTouchEvent(event);
        return true;
    }

    public boolean onDown(@NonNull MotionEvent e) {
        return false;
    }

    public void onShowPress(@NonNull MotionEvent e) {
    }

    public void onLongPress(@NonNull MotionEvent e) {
    }

    public boolean onSingleTapUp(@NonNull MotionEvent e) {
        return false;
    }

    public boolean onScroll(MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

}