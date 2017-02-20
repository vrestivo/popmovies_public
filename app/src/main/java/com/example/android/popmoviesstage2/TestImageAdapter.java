package com.example.android.popmoviesstage2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * Image Adapter Class for testing puposes
 */

public class TestImageAdapter extends BaseAdapter {

    private Context mContext;

    public TestImageAdapter(Context c) {
        super();
        mContext = c;
    }

    @Override
    public int getCount() {
        return mImangeArray.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        //based on prior code feeback, checking for ViewType
        if(convertView == null || !(convertView instanceof ImageView)){

            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);

            imageView = (ImageView) layoutInflater.inflate(R.layout.grid_item_image_view, parent, false);

//            imageView.setLayoutParams(new GridView.LayoutParams(85,85));
//            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            imageView.setPadding(8,8,8,8);
        }
        else{
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mImangeArray[position]);
        return imageView;
    }

    private Integer[] mImangeArray = {
            R.drawable.sample_0,
            R.drawable.sample_1,
            R.drawable.sample_2,
            R.drawable.sample_3,
            R.drawable.sample_4,
            R.drawable.sample_5,
            R.drawable.sample_6,
            R.drawable.sample_7
    };


}
