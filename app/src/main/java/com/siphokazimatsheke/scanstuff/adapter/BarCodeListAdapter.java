package com.siphokazimatsheke.scanstuff.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.siphokazimatsheke.scanstuff.R;
import com.siphokazimatsheke.scanstuff.datasource.DBDataSourceManager;
import com.siphokazimatsheke.scanstuff.models.BarCodeModel;

import java.util.List;

public class BarCodeListAdapter extends ArrayAdapter<BarCodeModel> {
    private static int layoutId = R.layout.bar_code_list_adapter_item;
    private final Context context;

    private int selectedIndex;
    private List<BarCodeModel> barCodeModelList;
    private boolean isCurrent = true;
    private View view;

    public BarCodeListAdapter(Context context, List<BarCodeModel> barCodeModelList) {
        super(context, layoutId, barCodeModelList);
        this.context = context;
        this.barCodeModelList = barCodeModelList;
        selectedIndex = -1;
    }

    @Override
    public int getCount() {
        return barCodeModelList.size();
    }

    @Override
    public BarCodeModel getItem(int position) {
        return barCodeModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(layoutId, null);

        holder = new ViewHolder();
        final BarCodeModel model = getItem(position);

        holder.deleteImageView = (ImageView) view.findViewById(R.id.ivDeleteSelectedBarCode);

        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog(model);
            }
        });

        holder.tvBarCode = (TextView) view.findViewById(R.id.tvBarCode);
        holder.tvBarCode.setText(model.getScannedBarCodeNumber());

        holder.txtBarCodeCount = (TextView) view.findViewById(R.id.tvBarCodeCount);
        holder.txtBarCodeCount.setText(convertIntegerToString(model.getCountBarCode()));

        return view;
    }

    public String convertIntegerToString(Integer value) {
        if (value == null) {
            value = 0;
        }
        return String.valueOf(value);
    }

    private void createDialog(final BarCodeModel model) {
        AlertDialog.Builder cleanDataDialog = new AlertDialog.Builder(getContext(), R.style.CustomDialogTheme);
        cleanDataDialog.setTitle("Bar Code");
        cleanDataDialog.setMessage("Are you sure you want to delete this Bar Code?");

        cleanDataDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onClick(DialogInterface dialog, int which) {
                DeleteTest(model);
                loadBarCode();
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        cleanDataDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = cleanDataDialog.create();
        alert.getWindow().setTitleColor(getContext().getResources().getColor(R.color.white));
        alert.getWindow().setLayout(600, 400);
        alert.show();
    }

    public void DeleteTest(BarCodeModel deleteBarCodeModel) {
        DBDataSourceManager.getInstance().deleteSelectedBarCode(deleteBarCodeModel);
    }

    private void loadBarCode() {
        this.barCodeModelList = DBDataSourceManager.getInstance().loadBarCodeList();
    }

    private class ViewHolder {
        ImageView deleteImageView;
        TextView tvBarCode;
        TextView txtBarCodeCount;
    }
}
