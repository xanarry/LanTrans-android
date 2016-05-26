package file_and_dir_selector.bean;

/**
 * Created by AlexCheung on 2016/2/2.
 */
public class FileBean {
    /**文件 | 文件夹  路径*/
    public String path;
    /**文件 | 文件夹  名称*/
    public String name;
    public boolean isFile;
    public boolean isImage;
    /**展示 选中框*/
    public boolean isShowEditor;
    public boolean isSelected;
    public int resId;
    public enum FileStatus{
        /**空 文件夹*/
        tempFolder,
        /**空 文件*/
        tempFile,
        /**是 文件*/
        isFile,
        /**是 文件夹*/
        isFolder
    }
}
