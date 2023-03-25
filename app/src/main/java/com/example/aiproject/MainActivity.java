package com.example.aiproject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.Surface;
import android.view.View;
import android.webkit.WebStorage;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class MainActivity extends AppCompatActivity {


    FaceDetectorOptions highAccuracyOpts =
            new FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                    .build();

    FaceDetector detector = FaceDetection.getClient(highAccuracyOpts);



    private final static int scaling_Facter=10;

    //현재 이미지가 올라와 있는지 여부를 확인
    boolean inImg=false;

    Button btn1;
    ImageButton btn3;
    Button btn4;
    File file;
    String filePath;
    String faceinfo=null;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView=findViewById(R.id.bottom_navigation);

        btn1=findViewById(R.id.img_button);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent,1);
                BitmapDrawable bitmapDrawable=(BitmapDrawable)btn3.getDrawable();
                Bitmap bitmap=bitmapDrawable.getBitmap();
                faceinfo=analyzePicture(bitmap);
                inImg=true;
            }
        });

        ActivityResultLauncher<Intent> requestCameraFileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    if (bitmap != null) {
                        btn3.setImageBitmap(bitmap);
                    }
                });
        btn3=findViewById(R.id.cma);
        btn3.setOnClickListener(v -> {
            //camera app......................
            //파일 준비...............
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                File file = File.createTempFile(
                        "JPEG_" + timeStamp + "_",
                        ".jpg",
                        storageDir
                );
                filePath = file.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(
                        this,
                        "com.example.aiproject.fileprovider",
                        file
                );
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                requestCameraFileLauncher.launch(intent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            BitmapDrawable bitmapDrawable=(BitmapDrawable)btn3.getDrawable();
            Bitmap bitmap=bitmapDrawable.getBitmap();
            faceinfo=analyzePicture(bitmap);
            inImg=true;
        });

        //이거는 테스트용 초기화면으로 넘어가기 위한 인텐트 입니다.(비트맵 형식의 이미지, 분석된 얼굴의 좌표값을 같이 보냄)
        btn4=findViewById(R.id.test_screen);
        btn4.setOnClickListener(new View.OnClickListener() {
            //비트맵 이미지를 파일 형태로 변환
            public File BmpToFile(Bitmap bmp, String filename)
            {
                File file=new File(getExternalFilesDir(null),filename);
                try{
                    FileOutputStream fos=new FileOutputStream(file);
                    bmp.compress(Bitmap.CompressFormat.PNG,100,fos);
                    fos.flush();
                    fos.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
                return file;
            }

            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,InitialActivity.class);
                BitmapDrawable bitmapDrawable=(BitmapDrawable)btn3.getDrawable();
                Bitmap bitmap=bitmapDrawable.getBitmap();
                File file=BmpToFile(bitmap,"image.png");
                intent.putExtra("img",file.getAbsolutePath());
                intent.putExtra("imginfo",faceinfo);
                startActivity(intent);
            }
        });



        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item->{

            switch (item.getItemId()){

                case R.id.bottom_menu_1:
                {
                    Toast.makeText(this,"고객님의 이미지로 스타일 진단 화면으로 이동합니다.(사진등록을 먼저해야합니다.)",Toast.LENGTH_SHORT).show();
                    break;
                }
                case R.id.bottom_menu_2:
                {
                    Toast.makeText(this,"스타일 검색으로 이동합니다.(고객님이 원하는 태그를 입력해주세요)", Toast.LENGTH_SHORT).show();
                    break;

                }
                case R.id.bottom_menu_3:
                {
                    Toast.makeText(this,"당신의 회원정보를 보여줍니다.", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
            return false;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    btn3.setImageURI(uri);
                }
                break;
        }

        if(requestCode == 101  && resultCode == Activity.RESULT_OK){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            btn3.setImageBitmap(bitmap);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }


    //mlkit의 face_detection의 기능을 실행하는 메소드이다.
    private String analyzePicture(Bitmap bitmap){
        Toast.makeText(this,"최소 100X100에서 최대 1280X1280정도 크기의 이미지를 올려주세요",Toast.LENGTH_SHORT).show();
        //이미지 크기를 자동으로 조정
        Bitmap smbitmap=bitmap;
        if(smbitmap.getHeight()>1280||smbitmap.getWidth()>1280){
        smbitmap=Bitmap.createScaledBitmap(
                bitmap,
                bitmap.getWidth()/scaling_Facter,
                bitmap.getHeight()/scaling_Facter,
                false
        );
        }
        else if(smbitmap.getHeight()<100||smbitmap.getWidth()<100){
            smbitmap=Bitmap.createScaledBitmap(
                    bitmap,
                    bitmap.getWidth()*scaling_Facter,
                    bitmap.getHeight()*scaling_Facter,
                    false
            );
        }
        else{
            smbitmap=Bitmap.createScaledBitmap(
                    bitmap,
                    bitmap.getWidth()/1,
                    bitmap.getHeight()/1,
                    false
            );
        }

        InputImage inputImage=InputImage.fromBitmap(smbitmap,0);

        detector.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                    @Override
                    public void onSuccess(List<Face> faces) {


                        faceinfo=String.valueOf(faces);

                        // 여기에 모든 색과 관련된 정보 추출후 서버로 보낸다.
                        detectface(bitmap,faces,String.valueOf(faces));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("yoon",e.toString());
                        Log.d("yoon","알 수 없는 원인으로 에러가 발생!!! 메일을 통해 문의");
                    }
                });
        return faceinfo;

    }
    //서버로 정보를 보내고 서버에서는 그 정보를 받아서 해석하고 나서, 해당 정보를 고객 한테 보내준다.
    private void detectface(Bitmap bitmap,List<Face> faces, String info){
        Log.d("yoon","해당 사진과, 감지된 정보를 서버로 보냅니다.");

    }


}
