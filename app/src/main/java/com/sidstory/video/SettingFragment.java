package com.sidstory.video;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.coder.zzq.smartshow.toast.SmartToast;
import com.sidstory.video.Utils.Feedback;
import com.suke.widget.SwitchButton;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobUpdateListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.update.BmobUpdateAgent;
import cn.bmob.v3.update.UpdateResponse;
import cn.bmob.v3.update.UpdateStatus;


public class SettingFragment extends Fragment implements SwitchButton.OnCheckedChangeListener {
    private SwitchButton wifidialog;
    private SwitchButton closeSplash;
    Button updateButton;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    EditText feedback_info;
    EditText feedback_connect;
    Button submit;
    String msg;

    public SettingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_settings,container,false);
       wifidialog=view.findViewById(R.id.switch_wifidialog);
       updateButton=view.findViewById(R.id.setting_checkupdate);
       closeSplash=view.findViewById(R.id.switch_closesplash);
        feedback_info=view.findViewById(R.id.feedback_info);
        feedback_connect=view.findViewById(R.id.feedback_connect);
        submit=view.findViewById(R.id.feedback_submit);
       preferences= getContext().getSharedPreferences("settings",Context.MODE_PRIVATE);
       editor=preferences.edit();
       closeSplash.setOnCheckedChangeListener(this);
       wifidialog.setOnCheckedChangeListener(this);
       submit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               String content = feedback_info.getText().toString();
               String info=feedback_connect.getText().toString();
               if(!TextUtils.isEmpty(content)&&!TextUtils.isEmpty(info)){
                   if(content.equals(msg)){
                       SmartToast.warning("请勿重复提交反馈");
                   }else if (content.contains("傻逼")||content.contains("尼玛")||content.contains("妈")||content.contains("全家")||content.contains("垃圾")||content.contains("狗日")||content.contains("狗")||content.contains("爹")||content.contains("爷爷")||content.contains("祖宗")){
                       SmartToast.warning("你个傻逼，你骂谁呢？");
                   }
                   else {
                       msg=content;
                       // 发送反馈信息
                        saveFeedbackMsg(content,info);

                   }
               }else {
                   SmartToast.warning("请填写反馈内容");
               }
           }
       });
       updateButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               BmobUpdateAgent.setUpdateListener(new BmobUpdateListener() {
                   @Override
                   public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
                       // TODO Auto-generated method stub
                       if (updateStatus == UpdateStatus.Yes) {//版本有更新

                       }else if(updateStatus == UpdateStatus.No){
                           SmartToast.complete("版本无更新");
                       }else if(updateStatus==UpdateStatus.IGNORED){
                           SmartToast.complete("该版本已被忽略更新");
                       }else if(updateStatus==UpdateStatus.TimeOut){
                           SmartToast.complete("查询出错或查询超时");
                       }
                   }
               });
               BmobUpdateAgent.forceUpdate(getContext());
           }
       });
       init();
        return view;
    }

    private void init() {
        if (preferences.getBoolean("wifidialog", false)) {
            wifidialog.setChecked(true);
        }
    }


    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        switch (view.getId()){
            case R.id.switch_wifidialog:{
                editor.putBoolean("wifidialog",isChecked).commit();
                break;
            }
            case R.id.switch_closesplash:{
                editor.putBoolean("splashclose",isChecked).commit();
                break;
            }

}
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BmobUpdateAgent.setUpdateListener(null);
    }
    /**
     * 保存反馈信息到Bmob云数据库中
     * @param msg 反馈信息
     */
    private void saveFeedbackMsg(String msg,String connect){
        Feedback feedback = new Feedback();
        feedback.setContent(msg);
        feedback.setContacts(connect);
        feedback.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    SmartToast.success("反馈提交成功!");
                }
                else SmartToast.fail("反馈提交失败！");
            }
        });

    }

}
