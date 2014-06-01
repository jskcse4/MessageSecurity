package com.message.messagesecurity;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messagesecurity.R;

public class SMSReceiver extends Activity {
	private Button DecryButton;
	private Button CloseButton;
	private TextView textView1;	
	private CharSequence plaintext;
	private final static byte[] salt = {(byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c, (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99};
	
	private BroadcastReceiver intentReceiver = new BroadcastReceiver()
	{
		public void onReceive(Context context, Intent intent) {
			Intent intent1= getIntent();//get message here
			String strMessage = intent1.getStringExtra("sms");
			textView1=(TextView) findViewById(R.id.textView1);
			textView1.setText(strMessage);
		}
	};
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rec);
		CloseButton = (Button) findViewById(R.id.CloseButton);
		DecryButton = (Button) findViewById(R.id.button1);
		
		CloseButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				finish();
				System.exit(1000);				
			}			
		});
		DecryButton.setOnClickListener(new OnClickListener() {

			@Override
            public void onClick(View v) 
            {
            	String phone = getMy10DigitPhoneNumber();
            	char[] password = phone.toCharArray();
            	
            	SecretKeyFactory factory;
				try {
					factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
					KeySpec spec = new PBEKeySpec(password, salt, 1024, 256);
					SecretKey tmp = factory.generateSecret(spec);
					SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
					String Receivedtext = textView1.getText().toString();
					byte[] ciphertext = Receivedtext.getBytes();
					Cipher cipher = null;
					cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
					cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(salt));
					plaintext = new String(cipher.doFinal(ciphertext), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidAlgorithmParameterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	textView1.setText(plaintext);
            	
            	Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
            }
			
			private String getMyPhoneNumber(){
				TelephonyManager mTelephonyMgr;
				mTelephonyMgr = (TelephonyManager)
		        getSystemService(Context.TELEPHONY_SERVICE); 
				return mTelephonyMgr.getLine1Number();
			}

			private String getMy10DigitPhoneNumber(){
				String s = getMyPhoneNumber();
				return s.substring(2);
			}
		});		
	}
		protected String getMy10DigitPhoneNumber() {
		
		return null;
	}
		public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu; this adds items to the action bar if it is present.
	        getMenuInflater().inflate(R.menu.main, menu);
	        return true;
	    }
		public BroadcastReceiver getIntentReceiver() {
			return intentReceiver;
		}
		public void setIntentReceiver(BroadcastReceiver intentReceiver) {
			this.intentReceiver = intentReceiver;
		}
}
