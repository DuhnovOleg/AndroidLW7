package com.example.lw7;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SecondActivity extends Activity
{
    String login;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Context context = this;
        final Handler h = new Handler();

        Bundle bundle = getIntent().getExtras();
        login = bundle.get("login").toString();

        Button buttonAdd        = findViewById(R.id.buttonAdd);
        Button buttonDelete     = findViewById(R.id.buttonDelete);
        Button buttonDeleteUser = findViewById(R.id.buttonDeleteUser);
        Button buttonChange     = findViewById(R.id.buttonChange);
        Button buttonBack       = findViewById(R.id.buttonBack);

        DatabaseHandler db = new DatabaseHandler(this);

        ListView listView = findViewById(R.id.textList);

        ArrayList<String> selectedUsers = new ArrayList<String>();
        ArrayList<String> stringArray = new ArrayList<String>();

        intent = new Intent(SecondActivity.this, MainActivity.class);

        final Looper looper = Looper.getMainLooper();
        final Message message = Message.obtain();
        final Handler handler = new Handler(looper);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, stringArray);
        listView.setAdapter(adapter);

        Toast.makeText(this, "Привет, " + login, Toast.LENGTH_SHORT).show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                String user = (String) adapter.getItem(position);
                if (listView.isItemChecked(position))
                    selectedUsers.add(user);
                else
                    selectedUsers.remove(user);
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v)
            {
                EditText editText = findViewById(R.id.editText);
                String userData = editText.getText().toString();

                adapter.add(userData);
                adapter.notifyDataSetChanged();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v)
            {
                for (int i = 0; i < selectedUsers.size(); i++)
                {
                    adapter.remove(selectedUsers.get(i));
                }

                listView.clearChoices();
                selectedUsers.clear();

                adapter.notifyDataSetChanged();
            }
        });

        buttonDeleteUser.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v)
            {
                EditText editText = findViewById(R.id.editText);
                String userData = editText.getText().toString();
                List<User> userList = db.getAllUsers();

                for (User user : userList)
                {
                    if (Objects.equals(user._login, userData))
                    {
                        new ThreadTask(handler).DeleteUser(db, user);
                        try {
                            Thread.sleep(1200);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        startActivity(intent);
                        return;
                    }
                }
                Toast.makeText(context, "Пользователь с логином" + userData + "не найден!", Toast.LENGTH_SHORT).show();
            }
        });

        buttonChange.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v)
            {
                EditText editText = findViewById(R.id.editText);
                String text = editText.getText().toString();

                List<User> userList = db.getAllUsers();

                for (User tempUser : userList)
                {
                    if (Objects.equals(login, tempUser._login))
                    {
                        db.ChangePassword(tempUser, text);
                        new ThreadTask(handler).ChangePassword(db, tempUser, text);
                        Toast.makeText(context, "Пароль успешно изменён!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener()
        {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v)
            {
                startActivity(intent);
            }
        });
    }
}
