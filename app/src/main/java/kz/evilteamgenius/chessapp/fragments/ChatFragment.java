package kz.evilteamgenius.chessapp.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Arrays;

import kz.evilteamgenius.chessapp.Const;
import kz.evilteamgenius.chessapp.R;
import kz.evilteamgenius.chessapp.StompUtils;
import kz.evilteamgenius.chessapp.models.MoveMessage;
import kz.evilteamgenius.chessapp.models.enums.MoveMessageType;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompCommand;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.dto.StompMessage;

import static android.content.Context.MODE_PRIVATE;

@SuppressWarnings({"FieldCanBeLocal", "ResultOfMethodCallIgnored", "CheckResult"})
public class ChatFragment extends Fragment {

    TextView chatContent;
    Button btnSend;
    EditText etMessage;

    void init(View v) {
        chatContent = v.findViewById(R.id.tvContent);
        btnSend = v.findViewById(R.id.btnSend);
        etMessage = v.findViewById(R.id.etMessage);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_chat, null);
        System.out.println("Chat Activity started!");
        init(v);
        StompClient stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, Const.address);
        StompUtils.lifecycle(stompClient);
        System.out.println("Start connecting to server");
        // Connect to WebSocket server
        stompClient.connect();

        // 订阅消息
        System.out.println("Subscribe broadcast endpoint to receive response");
        String dest = Const.makeMoveResponse.replace(Const.placeholder,getUsername());
        System.out.println(dest + "   12321312321312312");
        stompClient.topic(dest).subscribe(stompMessage -> {
            System.out.println("1");
            JSONObject jsonObject = new JSONObject(stompMessage.getPayload());
            System.out.println("2");

            System.out.println("Receive: " + jsonObject.toString());
            System.out.println("3");

            chatContent.append(jsonObject.toString() + "\n");
            System.out.println("4");

        },throwable -> Log.e("Chat Fragment", "Throwable " + throwable.getMessage()));

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("clicked!");

                MoveMessage moveMessage = new MoveMessage(1, 1, 2, 2, etMessage.getText().toString(), MoveMessageType.OK);
                Gson gson = new Gson();
                String json = gson.toJson(moveMessage);
                System.out.println("A");
                stompClient.send(new StompMessage(
                        // Stomp command
                        StompCommand.SEND,
                        // Stomp Headers, Send Headers with STOMP
                        // the first header is required, and the other can be customized by ourselves
                        Arrays.asList(
                                new StompHeader(StompHeader.DESTINATION, Const.makeMoveAddress),
                                new StompHeader("authorization", "this is a token generated by your code!")
                        ),
                        // Stomp payload
                        json)
                ).subscribe();
                System.out.println("B");
                etMessage.setText("");

            }
        });

        return v;
    }


    public String getUsername() {
        SharedPreferences preferences = this.getActivity().getSharedPreferences("myPrefs", MODE_PRIVATE);
        return preferences.getString("username", null);
    }


}
