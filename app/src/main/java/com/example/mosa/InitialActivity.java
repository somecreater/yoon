package com.example.mosa;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;


import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.io.FileNotFoundException;

public class InitialActivity extends AppCompatActivity {

    ImageView img1;
    ImageView img2;

    Button btn1;
    Button btn2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommended_initial_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        img1=findViewById(R.id.example_skin_img);
        img2=findViewById(R.id.example_face_img);

        Intent intent=getIntent();
        String imagePath = intent.getStringExtra("img");
        File file=new File(imagePath);
        Uri uri= FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID+".fileprovider",file);
        String faceinfo=intent.getStringExtra("imginfo");

        try{
            Bitmap bitmap=BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            img1.setImageBitmap(bitmap);
            img2.setImageBitmap(bitmap);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item->{
            switch (item.getItemId()){
                case R.id.bottom_menu_1:
                {

                }
                case R.id.bottom_menu_2:
                {
                    //스타일 검색 화면을 보여준다.
                }
                case R.id.bottom_menu_3:
                {
                    //회원님의 정보를 보여준다
                }
            }
            return false;
        });
        btn1=findViewById(R.id.color_btn);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_1=new Intent(InitialActivity.this,PersonalActivity.class);
                //if 파이어 베이스에서 String 형으로 보내줄 경우
                //String face_color_info_1=sendimg_skin_firebase(bitmap,faceinfo)
                //if 파이어 베이스에서 Json 형으로 보내줄 경우
                //JSONObject face_color_info_1=(JSONObject)sendimg_skin_firebase(bitmap,faceinfo)
                //String info_rem=sendimg_recom_firebase(bitmap,faceinfo)
                //위의 퍼스널 컬러 정보와 추천정보를 담아서 해당 엑티비티에 전달(내 생각인데 큰 파일형식(?)이 좋을듯)
                //일단 모르기 때문에 String 형으로 아무거나 전달
                String info_rem="테스트용 추천정보";
                String face_color_info_1="테스트용 제목";
                String face_color_info_2="테스트용 내용";//여기에는 제목(퍼스널 컬러)에 알맞는 정보(내용)를 보내줘야
                intent_1.putExtra("title",face_color_info_1);
                intent_1.putExtra("detail",face_color_info_2);
                intent_1.putExtra("recommend",info_rem);
                startActivity(intent_1);
            }
        });
        btn2=findViewById(R.id.face_btn);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_2=new Intent(InitialActivity.this,FaceDesActivity.class);
                //if 파이어 베이스에서 String 형으로 보내줄 경우
                //String info=sendimg_shap_firebase(bitmap,faceinfo)
                //if 파이어 베이스에서 Json 형으로 보내줄 경우
                //JSONObject jsonob=(JSONObject)sendimg_shap_firebase(bitmap,faceinfo)
                //일단 모르기 때문에 String 형으로 아무거나 전달
                String face_shape_info_1="테스트용 제목";
                String face_shape_info_2="테스트용 내용입니다.";
                intent_2.putExtra("title",face_shape_info_1);
                intent_2.putExtra("detail",face_shape_info_2);
                startActivity(intent_2);
            }
        });

    }

    public void sendimg_skin_firebase(Bitmap bmp, String faceinfo){
        //여기는 차후 구현해야(파이어베이스에 해당 이미지를 보내서 추천하는 퍼스널 컬러와 관련된 정보를 얻는다.)
        //여기의 반환형은 달라진다(파이어베이스에서 보내주는 정보의 형태(json,String,...)에 따라서 달라짐)
        Log.d("User","피부색을 분석해서 당신의 컬러를 알려드리겠습니다.");
    }
    public void sendimg_shap_firebase(Bitmap bmp, String faceinfo){
        //여기는 차후 구현해야(파이어베이스에 해당 이미지를 보내서 얼굴형과 관련된 정보를 얻는다.)
        //여기의 반환형은 달라진다(파이어베이스에서 보내주는 정보의 형태(json,String,...)에 따라서 달라짐)
        Log.d("User","얼굴의 좌표값을 분석해서 당신의 얼굴형을 알려드리겠습니다.");
    }
    public void sendimg_recom_firebase(Bitmap bmp, String faceinfo){
        String info_shap; //sendimg_shap_firebase(고객의 얼굴형 정보를 이용해야)
        String info_color; //sendimg_skin_firebase(고객의 추천된 퍼스널컬러 정보를 이용해야)
        //여기는 차후 구현해야(파이어베이스에 해당 이미지를 보내서 관련 추천정보를 얻어온다.)
        //여기의 반환형은 달라진다(파이어베이스에서 보내주는 정보의 형태(json,String,...)에 따라서 달라짐)
        Log.d("User","당신의 컬러를 바탕으로 옷,화장품,헤어,악세사리를 추천합니다.");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.top_menu,menu); //우상단 메뉴 활성화
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // 뒤로가기 버튼을 눌렀을 때 , 바로 이전 화면으로 이동
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}