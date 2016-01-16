package com.eap.sdy51.ge2015;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by alepapadop on 1/9/16.
 */
public class CustomViewObject {

    private DeviceState _ds;
    public static enum VIEW_TYPE  {NONE, SWITCH, SEEKBAR, RADIO };
    private VIEW_TYPE _view_type;
    private View _view = null;
    private View _complete_view = null;
    private Context _context;
    private final int padding = 30;
    private final int text_size = 25;

    public int get_state_id_() {
        return _ds.stateid;
    }

    public DeviceState get_device_state() {
        return _ds;
    }

    CustomViewObject(Context context, DeviceState ds) {
        _ds = ds;
        _context = context;
    }

    public VIEW_TYPE custom_view_get_view_type() {
        return _view_type;
    }

    private int get_num_of_state_vals(DeviceState ds) {
        return (ds.max - ds.min + 1) / ds.value_step;
    }

    private VIEW_TYPE view_type(DeviceState ds) {

        int num_of_state_vals = get_num_of_state_vals(ds);

        if (num_of_state_vals > 0 && num_of_state_vals <= 2) {
            return VIEW_TYPE.SWITCH;
        } else if (num_of_state_vals > 2 && num_of_state_vals <= 4) {
            return VIEW_TYPE.RADIO;
        } else if (num_of_state_vals > 4) {
            return VIEW_TYPE.SEEKBAR;
        } else {
            assert false;
            return VIEW_TYPE.NONE;
        }

    }

    private void init_switch(Switch sw, DeviceState ds) {

        if (ds.current_value == 1) {
            sw.setChecked(true);
        } else {
            sw.setChecked(false);
        }

    }

    private Switch create_switch(Context context, DeviceState ds) {

        Switch sw = new Switch(_context);
        sw.setText(ds.state_name);
        sw.setTextColor(Color.BLACK);
        sw.setTextSize(text_size);

        init_switch(sw, ds);

        return sw;
    }


    private void init_radio_group(RadioGroup rg, DeviceState ds) {
        int num_of_state_vals = get_num_of_state_vals(ds);
        int id = ds.min;

        for (int i = 0; i < num_of_state_vals; ++i) {
            RadioButton rb = new RadioButton(_context);
            rb.setTextColor(Color.BLACK);
            rb.setText(ds.state_name + " " + id);
            rb.setId(id);
            rg.addView(rb);
            if (id == ds.current_value) {
                rb.setChecked(true);

            }

            ++id;
        }

    }

    private RadioGroup create_radio_group(Context context, DeviceState ds) {
        RadioGroup rg = new RadioGroup(_context);
        rg.setOrientation(LinearLayout.HORIZONTAL);


        init_radio_group(rg, ds);


        return rg;
    }

    private void init_seek_bar(SeekBar sb, DeviceState ds) {
        sb.setMax(ds.max);
        sb.setProgress(ds.current_value);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        sb.setLayoutParams(lp);

    }

    private SeekBar create_seek_bar(Context context, DeviceState ds) {
        SeekBar sb = new SeekBar(context);



        init_seek_bar(sb, ds);

        return sb;
    }


    private View construct_complete_view(Switch sw, DeviceState ds) {
        LinearLayout ll = new LinearLayout(_context);

        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(padding, padding, padding, padding);

        ll.addView(sw);

        return ll;

    }

    private View construct_complete_view(RadioGroup rg, DeviceState ds) {
        LinearLayout ll = new LinearLayout(_context);

        ll.setOrientation(LinearLayout.VERTICAL);
        TextView text = new TextView(_context);
        text.setText(ds.state_name);
        text.setTextColor(Color.BLACK);
        text.setPadding(padding / 2, padding / 2, padding / 2, padding / 2);
        text.setTextSize(text_size);

        ll.setPadding(padding, padding, padding, padding);

        ll.addView(text);
        ll.addView(rg);

        return ll;
    }

    private View construct_complete_view(SeekBar sk, DeviceState ds) {
        LinearLayout ll = new LinearLayout(_context);

        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(padding, padding, padding, padding);

        ll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        TextView text = new TextView(_context);
        text.setText(ds.state_name + " " + ds.current_value + "%");
        text.setTextColor(Color.BLACK);
        text.setTextSize(text_size);
        text.setId(ds.stateid);
        ll.addView(text);


        ll.addView(sk);

        return ll;

    }

    public View custom_view_get_widget() {

        _view_type = view_type(_ds);

        if (_view != null) {
            return _view;
        }

        switch (_view_type) {
            case SWITCH:
                _view = create_switch(_context, _ds);

                break;
            case RADIO:
                _view = create_radio_group(_context, _ds);

                break;
            case SEEKBAR:
                _view = create_seek_bar(_context, _ds);

                break;
            default:
                _view = null;

                assert false;
        }

        return _view;
    }

    public View custom_view_get_complete_widget() {

        _view_type = view_type(_ds);

        if (_complete_view != null) {
            return _complete_view;
        }

        if (_view == null) {
            custom_view_get_widget();
        }

        switch (_view_type) {
            case SWITCH:
                _complete_view = construct_complete_view((Switch)_view, _ds);
                break;
            case RADIO:
                _complete_view = construct_complete_view((RadioGroup) _view, _ds);
                break;
            case SEEKBAR:
                _complete_view = construct_complete_view((SeekBar) _view, _ds);
                break;
            default:
                _complete_view = null;
                assert false;
        }

        return _complete_view;
    }

}
