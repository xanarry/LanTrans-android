package com.xanarry.lantrans;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import file_and_dir_selector.adapter.FileSelectorAdapter;
import file_and_dir_selector.bean.FileBean;

/**
 * Created by AlexCheung on 2016/2/2.
 */
public class FileSelectorActivity extends Activity {
    /**
     * 获取 单个文件 的路径
     */
    public static int requestCodeSingleFile;
    /**
     * 从哪个Activity 到 FileSelectorActivity
     */
    public static String keyClassName = "keyClassName";
    /**
     * 在 onActivityResult 中获取 文件的全路径
     */
    public static String keyFilePaths = "keyFilePaths";
    /**
     * true 选择文件路径； fasle选择文件夹路径 默认为true
     */
    public static String keyIsSelectFile = "keyIsSelectFile";
    /**
     * 只想通过 FileSelectorActivity 进行单选  默认为 true
     */
    public static String keyIsSingleSelector = "keyIsSingleSelector";
    private Context context;
    private String rootPath;
    private String className;
    private Class<?> clazz;
    private FileSelectorAdapter adapter;
    private LinearLayout layoutPath;
    private List<TextView> listTvPath;
    private boolean hasClicked;
    /**
     * 选择文件
     */
    private boolean isSelectFile;
    /**
     * 单选
     */
    private boolean isSingleSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector_file);
        hasClicked = false;
        context = this;
        listTvPath = new ArrayList<TextView>();
        rootPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        adapter = new FileSelectorAdapter(context);
        Intent intent = getIntent();
        className = intent.getStringExtra(keyClassName);
        isSelectFile = intent.getBooleanExtra(keyIsSelectFile, true);
        TextView title = (TextView) findViewById(R.id.titleText);
        if (isSelectFile == true) {
            title.setText("选择发送的文件");
        } else {
            title.setText("选择保存目录");
        }
        isSingleSelector = intent.getBooleanExtra(keyIsSingleSelector, true);
        clazz = forName(className);
        File file = new File(rootPath);

        Log.e("root dir:", rootPath);

        if (!file.isDirectory()) {
            Intent data = new Intent(context, clazz);
            setResult(RESULT_OK, data);
            finish();
        }
        adapter.setIsSingleSelector(isSingleSelector);
        initView();
        loadJsonData(rootPath);
    }

    private void initView() {
        findViewById(R.id.iv_back).setOnClickListener(new MyOnClickListener(-1));
        findViewById(R.id.sbt).setOnClickListener(new MyOnClickListener(-1));
        TextView textView = (TextView) findViewById(R.id.tv_root_path);
        textView.setTag(rootPath);
        listTvPath.add(textView);
        layoutPath = (LinearLayout) findViewById(R.id.layout_path);
        findViewById(R.id.tv_root_path).setOnClickListener(new MyOnClickListener(0));
        ListView listView = (ListView) findViewById(R.id.lv);
        listView.setAdapter(adapter);
    }

    private final class MyOnClickListener implements View.OnClickListener {
        private int position;

        public MyOnClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            if (R.id.iv_back == v.getId()) {
                finish();
                return;
            } else if (R.id.sbt == v.getId()) {
                setResult();
                return;
            }
            if((listTvPath==null) ||(listTvPath.size()==0)){
                return ;
            }
            String filePath = (String) listTvPath.get(position).getTag();
            for (int i = listTvPath.size() - 1; (i >= 0) && (i >= position); i--) {
                layoutPath.removeView(listTvPath.get(i));
                listTvPath.remove(i);
            }
            if (position == 0) {
                TextView textView = (TextView) findViewById(R.id.tv_root_path);
                textView.setTag(rootPath);
                listTvPath.add(textView);
            }
            loadJsonData(filePath);
        }
    }

    public void loadJsonData(String filePath) {
        File mainFile = new File(filePath);
        if (!rootPath.equalsIgnoreCase(filePath)) {
            hasClicked = true;
            TextView tvPath = new TextView(context);
            tvPath.setTag(mainFile.getAbsolutePath());
            tvPath.setGravity(Gravity.CENTER);
            tvPath.setPadding(dpToPx(context, 16F), 0, dpToPx(context, 16F), 0);
            tvPath.setText(mainFile.getName());
            tvPath.setBackgroundResource(R.drawable.bg9_path_arrow);
            listTvPath.add(tvPath);
            layoutPath.addView(tvPath);
            tvPath.setOnClickListener(new MyOnClickListener(listTvPath.size() - 1));
        }
        File[] files = mainFile.listFiles();
        List<FileBean> listFileBean = new ArrayList<FileBean>();
        for (int i = 0; (files != null) && (i < files.length); i++) {
            File subFile = files[i];
            FileBean bean = new FileBean();
            bean.isSelected = false;
            if (subFile.isHidden()) {
                continue;
            }
            bean.path = subFile.getPath();
            String fileName = subFile.getName();
            if (subFile.isDirectory()) {
                bean.isShowEditor = !isSelectFile;
                bean.resId = R.mipmap.ic_folder;
                if ("DCIM".equalsIgnoreCase(fileName) || "Pictures".equalsIgnoreCase(fileName) || "Picture".equalsIgnoreCase(fileName) || "Camera".equalsIgnoreCase(fileName) || "photo".equalsIgnoreCase(fileName) || "screenshots".equalsIgnoreCase(fileName) || "Screenshot".equalsIgnoreCase(fileName)) {
                    bean.resId = R.mipmap.ic_folder_picture;
                } else if ("movie".equalsIgnoreCase(fileName) || "movies".equalsIgnoreCase(fileName) || "video".equalsIgnoreCase(fileName) || "videos".equalsIgnoreCase(fileName)) {
                    bean.resId = R.mipmap.ic_folder_video;
                } else if ("audio".equalsIgnoreCase(fileName) || "audios".equalsIgnoreCase(fileName) || "music".equalsIgnoreCase(fileName) || "musics".equalsIgnoreCase(fileName)) {
                    bean.resId = R.mipmap.ic_folder_music;
                }
            } else if (subFile.isFile()) {
                bean.isShowEditor = isSelectFile;
                String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                //KLog.e("fileName = "+fileName+" suffix = "+suffix);
                if ("log".equalsIgnoreCase(suffix) || "java".equalsIgnoreCase(suffix) || "txt".equalsIgnoreCase(suffix) || "text".equalsIgnoreCase(suffix) || "json".equalsIgnoreCase(suffix)) {
                    bean.resId = R.mipmap.ic_file_text;
                } else if ("doc".equalsIgnoreCase(suffix) || "docx".equalsIgnoreCase(suffix)) {
                    bean.resId = R.mipmap.ic_file_doc;
                } else if ("ppt".equalsIgnoreCase(suffix)) {
                    bean.resId = R.mipmap.ic_file_ppt;
                } else if ("xls".equalsIgnoreCase(suffix) | "xlsx".equalsIgnoreCase(suffix)) {
                    bean.resId = R.mipmap.ic_file_xls;
                } else if ("aac".equalsIgnoreCase(suffix) || "flac".equalsIgnoreCase(suffix) || "wav".equalsIgnoreCase(suffix) || "ape".equalsIgnoreCase(suffix) || "wma".equalsIgnoreCase(suffix) || "ogg".equalsIgnoreCase(suffix) || "mp3".equalsIgnoreCase(suffix)) {
                    bean.resId = R.mipmap.ic_file_music;
                } else if ("pdf".equalsIgnoreCase(suffix)) {
                    bean.resId = R.mipmap.ic_file_pdf;
                } else if ("html".equalsIgnoreCase(suffix) || "htm".equalsIgnoreCase(suffix) || "xml".equalsIgnoreCase(suffix)) {
                    bean.resId = R.mipmap.ic_file_html;
                } else if ("f4v".equalsIgnoreCase(suffix) || "3gp".equalsIgnoreCase(suffix) || "mkv".equalsIgnoreCase(suffix) || "wmv".equalsIgnoreCase(suffix) || "rm".equalsIgnoreCase(suffix) || "avi".equalsIgnoreCase(suffix) || "mp4".equalsIgnoreCase(suffix) || "rmvb".equalsIgnoreCase(suffix) || "flv".equalsIgnoreCase(suffix)) {
                    bean.resId = R.mipmap.ic_file_movie;
                } else if ("jpg".equalsIgnoreCase(suffix) || "jpeg".equalsIgnoreCase(suffix) || "png".equalsIgnoreCase(suffix) || "gif".equalsIgnoreCase(suffix) || "bmp".equalsIgnoreCase(suffix)) {
                    bean.isImage = true;
                } else {
                    bean.resId = R.mipmap.ic_file_unknown;
                }
            }
            bean.name = subFile.getName();
            listFileBean.add(bean);
        }
        adapter.refreshItem(listFileBean);
        //((ListView) findViewById(R.id.lv)).setSelection(0);
    }

    public void setResult() {
        Intent data = new Intent(context, clazz);
        ArrayList<String> list = new ArrayList<String>();
        List<FileBean> fileBeans = adapter.getList();
        Log.e("List", fileBeans.toString());
        for (int i = 0; (fileBeans != null) && (i < fileBeans.size()); i++) {
            FileBean bean = fileBeans.get(i);
            if (bean.isSelected) {
                list.add(fileBeans.get(i).path);
            }
        }

        if (list.size() == 0 && isSelectFile == false) {
            list.add(rootPath);
        }
        Log.e("Selected", list.toString());

        data.putStringArrayListExtra(keyFilePaths, list);
        setResult(RESULT_OK, data);
        finish();
    }

    private Class<?> forName(String className) {
        Class clazz;
        try {
            clazz = Class.forName(className);
            return clazz;
        } catch (ClassNotFoundException e) {
            Log.e("有异常: ", e.getMessage());
        }
        return null;
    }

    /**
     * 数据转换: dp---->px
     */
    private int dpToPx(Context context, float dp) {
        if (context == null) {
            return -1;
        }
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((listTvPath != null) && (listTvPath.size() >= 2)) {
                TextView lastTextView = listTvPath.get(listTvPath.size() - 1);
                TextView gotoTextView = listTvPath.get(listTvPath.size() - 2);
                String gotoFilePath = (String) gotoTextView.getTag();
                layoutPath.removeView(lastTextView);
                layoutPath.removeView(gotoTextView);
                listTvPath.remove(listTvPath.size() - 1);
                listTvPath.remove(listTvPath.size() - 1);
                loadJsonData(gotoFilePath);
                return true;
            }
            if ((listTvPath != null) && (listTvPath.size() >= 1) && hasClicked) {
                TextView lastTextView = listTvPath.get(listTvPath.size() - 1);
                layoutPath.removeView(lastTextView);
                listTvPath.remove(listTvPath.size() - 1);
                loadJsonData(rootPath);
                return true;
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
