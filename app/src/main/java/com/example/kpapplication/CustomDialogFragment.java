package com.example.kpapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class CustomDialogFragment extends DialogFragment {

    String display_name;
    String departament;
    String mail;
    String inner_phone;
    String mobile_phone;
    String room;
    String post;

    CustomDialogFragment(String display_name, String departament, String mail, String inner_phone, String mobile_phone, String room, String post){
        this.display_name = display_name;
        this.departament = departament;
        this.post = post;
        this.mail = mail;
        this.inner_phone = inner_phone;
        this.mobile_phone = mobile_phone;
        this.room = room;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String message = "ФИО:\n" + display_name + "\n\n" + "Отдел:\n" + departament;

        if(!post.isEmpty()){
            message += "\n\n" + "Должность:\n" + post;
        }
        if(!mail.isEmpty()){
            message += "\n\n" + "Почта:\n" + mail;
        }
        if(mobile_phone != null){
            message += "\n\n" + "Телефон:\n" + mobile_phone;
        }
        if(!inner_phone.isEmpty()){
            message += "\n\n" + "Внутренний телефон:\n" + inner_phone;
        }
        if(!room.isEmpty()){
            message += "\n\n" + "Кабинет:\n" + room;
        }

        return builder.setTitle("Информация о работнике").setMessage(message).create();
    }

}
