package com.example.beevacuate;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

public class DisasterTextActivity extends Activity {

    EditText edit;
    TextView text;
    Button btn_left;
    Button btn_right;
    int page=1;

    String[] state = {"서울특별시", "부산광역시", "대구광역시", "인천광역시", "광주광역시", "대전광역시", "울산광역시",
            "세종특별자치시", "경기도", "강원도","충청북도", "충청남도", "전라북도", "전라남도", "경상북도", "경상남도", "제주특별자치도",};
    String[][] cities ={{"종로구", "중구", "용산구", "성동구", "광진구", "동대문구", "중랑구", "성북구", "강북구", "도봉구", "노원구", "은평구", "서대문구", "마포구", "양천구", "강서구", "구로구", "금천구", "영등포구", "동작구", "관악구", "서초구", "강남구", "송파구", "강동구"},
            {"중구", "서구", "동구", "영도구", "부산진구", "동래구", "남구", "북구", "해운대구", "사하구", "금정구", "강서구", "연제구", "수영구", "사상구", "기장군"},
            {"중구", "동구", "서구", "남구", "북구", "수성구", "달서구", "달성군"},
            {"중구", "동구", "미추홀구", "연수구", "남동구", "부평구", "계양구", "서구", "강화군", "옹진군"},
            {"동구", "서구", "남구", "북구", "광산구"},
            {"동구", "중구", "서구", "유성구", "대덕구"},
            {"중구", "남구", "동구", "북구", "울주군"},
            {"세종특별자치시"},
            {"수원시", "성남시", "고양시", "용인시", "부천시", "안산시", "안양시", "남양주시", "화성시", "평택시", "의정부시", "시흥시", "파주시", "광명시", "김포시", "군포시", "광주시", "이천시", "양주시", "오산시", "구리시", "안성시", "포천시", "의왕시", "하남시", "여주시", "여주군", "양평군", "동두천시", "과천시", "가평군", "연천군"},
            {"춘천시", "원주시", "강릉시", "동해시", "태백시", "속초시", "삼척시", "홍천군", "횡성군", "영월군", "평창군", "정선군", "철원군", "화천군", "양구군", "인제군", "고성군", "양양군"},
            {"청주시", "충주시", "제천시", "보은군", "옥천군", "영동군", "진천군", "괴산군", "음성군", "단양군"},
            {"천안시", "공주시", "보령시", "아산시", "서산시", "논산시", "계룡시", "당진시", "금산군", "부여군", "서천군", "청양군", "홍성군", "예산군", "태안군"},
            {"전주시", "군산시", "익산시", "정읍시", "남원시", "김제시", "완주군", "진안군", "무주군", "장수군", "임실군", "순창군", "고창군", "부안군"},
            {"목포시", "여수시", "순천시", "나주시", "광양시", "담양군", "곡성군", "구례군", "고흥군", "보성군", "화순군", "장흥군", "강진군", "해남군", "영암군", "무안군", "함평군", "영광군", "장성군", "완도군", "진도군", "신안군"},
            {"포항시", "경주시", "김천시", "안동시", "구미시", "영주시", "영천시", "상주시", "문경시", "경산시", "군위군", "의성군", "청송군", "영양군", "영덕군", "청도군", "고령군", "성주군", "칠곡군", "예천군", "봉화군", "울진군", "울릉군"},
            {"창원시", "진주시", "통영시", "사천시", "김해시", "밀양시", "거제시", "양산시", "의령군", "함안군", "창녕군", "고성군", "남해군", "하동군", "산청군", "함양군", "거창군", "합천군"},
            {"제주시", "서귀포시"}};
    String key="발급 받은 인증키";

    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disaster_main);
        //검색부분 text
        edit= (EditText)findViewById(R.id.edit);
        //list 텍스트
        text= (TextView)findViewById(R.id.text);
        btn_left=(Button)findViewById(R.id.btn_left);
        btn_right=(Button)findViewById(R.id.btn_right);
    }

    //Button을 클릭했을 때 자동으로 호출되는 callback method
    public void mOnClick(View v){
        page = 1;
        switch( v.getId() ){
            case R.id.button:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        data= getXmlData();//아래 메소드를 호출하여 XML data를 파싱해서 String 객체로 얻어오기
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                text.setText(data); //TextView에 문자열  data 출력
                            }
                        });
                    }
                }).start();
                break;
        }
    }

    public void preclick(View v){ // 이전 버튼 클릭 리스너
        if(page>1){
            page -= 1;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                data= getXmlData();//아래 메소드를 호출하여 XML data를 파싱해서 String 객체로 얻어오기
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        text.setText(data); //TextView에 문자열  data 출력
                    }
                });
            }
        }).start();

    }

    public  void nextclick(View v){ // 다음 버튼 클릭 리스너
        page += 1;
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                data= getXmlData();//아래 메소드를 호출하여 XML data를 파싱해서 String 객체로 얻어오기
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        text.setText(data); //TextView에 문자열  data 출력
                    }
                });
            }
        }).start();
    }
    // 다어얼로그
    public void dialogclick(View v){
        // 다이얼로그 레이아웃 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(DisasterTextActivity.this);
        builder.setTitle("행정구역을 선택하세요");
        // 행정구역 버튼 목록 생성
        builder.setItems(state, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String selecteState = state[i];

                // 도시 목록 다이얼로그 생성
                AlertDialog.Builder cityDialogBuilder = new AlertDialog.Builder(DisasterTextActivity.this);
                cityDialogBuilder.setTitle(selecteState);

                String[] selectedCities = cities[i];
                cityDialogBuilder.setItems(selectedCities, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int cityposition) {
                        // 선택한 도시를 검색창에 넣기
                        edit.setText(state[i] + " "+ selectedCities[cityposition]);
                    }
                });
                // 시 목록 다이얼로그 생성하고 표시
                AlertDialog cityDialog = cityDialogBuilder.create();
                cityDialog.show();
            }
        });
        // 다이얼로그를 생성하고 표시
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    String getXmlData(){

        StringBuffer buffer=new StringBuffer();

        String str= edit.getText().toString();//EditText에 작성된 Text얻어오기
        String location = URLEncoder.encode(str);//한글의 경우 인식이 안되기에 utf-8 방식으로 encoding     //지역 검색 위한 변수


        String queryUrl="https://apis.data.go.kr/1741000/DisasterMsg4/getDisasterMsg2List?serviceKey=Phgpr4CbAWanl3A0vPZDRtZ4KvnkZP2IHWFTkItDG%2FcN5Y1pSKTshNrmaxcenDZnBCPMDkBP%2B%2F3YKxNZX4sp4Q%3D%3D&&pageNo="+page+"&numOfRows=7&location_name="+location+"&type=xml";

        try {
            URL url= new URL(queryUrl);//문자열로 된 요청 url을 URL 객체로 생성.
            InputStream is= url.openStream(); //url위치로 입력스트림 연결

            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser xpp= factory.newPullParser();
            xpp.setInput( new InputStreamReader(is, "UTF-8") ); //inputstream 으로부터 xml 입력받기

            String tag;

            xpp.next();
            int eventType= xpp.getEventType();

            while( eventType != XmlPullParser.END_DOCUMENT ){
                switch( eventType ){
                    case XmlPullParser.START_DOCUMENT:
                        buffer.append("파싱 시작...\n\n");
                        break;

                    case XmlPullParser.START_TAG:
                        tag= xpp.getName();//태그 이름 얻어오기

                        if(tag.equals("row")) ;// 첫번째 검색결과
                        else if(tag.equals("create_date")){
                            buffer.append("날짜 : ");
                            xpp.next();
                            buffer.append(xpp.getText());//addr 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("\n"); //줄바꿈 문자 추가
                        }

                        else if(tag.equals("location_name")){
                            buffer.append("지역 이름 :");
                            xpp.next();
                            buffer.append(xpp.getText());//cpId
                            buffer.append("\n");
                        }
                        else if(tag.equals("msg")){
                            buffer.append("내용 :");
                            xpp.next();
                            buffer.append(xpp.getText());//cpNm
                            buffer.append("\n");
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag= xpp.getName(); //태그 이름 얻어오기

                        if(tag.equals("row")) buffer.append("\n");// 첫번째 검색결과종료..줄바꿈

                        break;
                }

                eventType= xpp.next();
            }

        } catch (Exception e) {
            // TODO Auto-generated catch blocke.printStackTrace();
        }

        buffer.append(page);

        return buffer.toString();//StringBuffer 문자열 객체 반환

    }

}
