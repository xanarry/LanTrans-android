package file_and_dir_selector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xanarry.lantrans.FileSelectorActivity;
import com.xanarry.lantrans.R;

import java.util.ArrayList;
import java.util.List;

import file_and_dir_selector.bean.FileBean;


public class FileSelectorAdapter extends BaseAdapter {
    protected List<FileBean> list;
    protected Context context;
    public boolean isSingleSelector;

    public FileSelectorAdapter(Context context) {
        this.list = new ArrayList<FileBean>();
        this.context = context;
        isSingleSelector = true;
    }

    /**
     * 更新适配器
     */
    public void updateItem(FileBean obj, int position) {
        list.set(position, obj);
        notifyDataSetChanged();
    }

    public void updateItem(List<FileBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(FileBean obj) {
        list.add(obj);
        notifyDataSetChanged();
    }

    public void addItem(List<FileBean> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void refreshItem(List<FileBean> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (list == null || list.size() <= 0 || position >= list.size() || position < 0) {
            return;
        }
        this.list.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return (list == null) ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return (list == null) ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<FileBean> getList() {
        return list;
    }

    public void setIsSingleSelector(boolean isSingleSelector) {
        this.isSingleSelector = isSingleSelector;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (list == null || list.size() <= 0) {
            return convertView;
        }
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_file_selector, parent, false);
            holder.tv = (TextView) convertView.findViewById(R.id.tv);
            holder.ivEditor = (ImageView) convertView.findViewById(R.id.iv_editor);
            holder.ivFile = (ImageView) convertView.findViewById(R.id.iv_file_logo);
            holder.layoutBody = convertView.findViewById(R.id.layout_body);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        FileBean bean = list.get(position);
        holder.tv.setText(bean.name);
        if (bean.isImage) {
            Glide.with(context).load(bean.path).centerCrop().diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.ic_file_image).error(R.mipmap.ic_file_image).into(holder.ivFile);
        } else {
            holder.ivFile.setImageResource(bean.resId);
        }
        if (bean.isShowEditor) {
            holder.ivEditor.setVisibility(View.VISIBLE);
        } else {
            holder.ivEditor.setVisibility(View.INVISIBLE);
        }
        holder.ivEditor.setTag(bean.path);
        String idTag = (String) holder.ivEditor.getTag();
        if (idTag.equalsIgnoreCase(bean.path)) {
            holder.ivEditor.setSelected(bean.isSelected);
        }
        holder.ivEditor.setOnClickListener(new MyOnClickListener(position, true));
        holder.layoutBody.setOnClickListener(new MyOnClickListener(position, false));
        return convertView;
    }

    private final class MyOnClickListener implements View.OnClickListener {
        private int position;
        private boolean checkFile;

        public MyOnClickListener(int position, boolean checkFile) {
            this.position = position;
            this.checkFile = checkFile;
        }

        @Override
        public void onClick(View v) {
            FileBean bean = list.get(position);
            if (!checkFile && bean.isFile) {
                return; //文件不让打开, 如果是点的是条目, 但这个条目又是文件, 直接忽略
            }
            if (R.id.iv_editor == v.getId()) {
                bean.isSelected = !bean.isSelected;
                updateItem(bean, position);
                if (isSingleSelector) {
                    ((FileSelectorActivity) context).setResult();
                }
            } else if (R.id.layout_body == v.getId()) {
                ((FileSelectorActivity) context).loadJsonData(bean.path);
            }
        }
    }

    protected final class ViewHolder {
        public TextView tv;
        public ImageView ivEditor;
        public ImageView ivFile;
        public View layoutBody;
    }
}
