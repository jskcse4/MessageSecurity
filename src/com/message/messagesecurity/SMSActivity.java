package com.message.messagesecurity;

import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.List;

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
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.messagesecurity.R;

public class SMSActivity extends Activity {

		private static final int CONTACT_PICKER_RESULT = 0;
		int CONTACT_PIC=0;
		private EditText PhnNumber;
		private EditText plainMsg;
		private EditText encryptMsg;
		private Button ResetButton, CloseButton, SendButton;
		String SENT = "SMS_SENT";
		String DELIVERED = "SMS_DELIVERED";
		PendingIntent sentPI,deliveredPI;
		BroadcastReceiver smsSentReceiver, smsDeliveredReceiver;
		IntentFilter intentFilter;
		
		protected Key secret;
		
		private final static byte[] salt = {(byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c, (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99};
		@Override
		protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		sentPI = PendingIntent.getBroadcast(this,0,new Intent(SENT),0);
		deliveredPI = PendingIntent.getBroadcast(this,0,new Intent(DELIVERED),0);
		
		intentFilter = new IntentFilter();
		intentFilter.addAction("SMS_RECEIVED_ACTION");
		
		PhnNumber = (EditText) findViewById(R.id.editText);
		plainMsg = (EditText) findViewById(R.id.editText2);
		encryptMsg = (EditText) findViewById(R.id.editText3);
		ResetButton = (Button) findViewById(R.id.ResetButton);
		SendButton = (Button) findViewById(R.id.SendButton);
		CloseButton = (Button) findViewById(R.id.CloseButton);
      
		PhnNumber.setOnClickListener(new EditText.OnClickListener() {
			
			public void onClick(View v)
			{
				Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                startActivityForResult(intent, CONTACT_PIC);
			}
		});
		
		SendButton.setOnClickListener(new OnClickListener(){
			
			public void onClick(View v) {
				try {
					String number = null;
					String msg;
					number = PhnNumber.getText().toString();
            		msg = plainMsg.getText().toString();
            		Encrypt(msg,number);
					String sms = encryptMsg.getText().toString();
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(number, null, sms, null, null);
					Toast.makeText(getApplicationContext(), "SMS Sent!",Toast.LENGTH_SHORT).show();
				  }
				catch (InvalidKeyException e) {
					Toast.makeText(getApplicationContext(),"SMS faild",Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					Toast.makeText(getApplicationContext(),"SMS faild",Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					Toast.makeText(getApplicationContext(),"SMS faild",Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (InvalidParameterSpecException e) {
					Toast.makeText(getApplicationContext(),"SMS faild",Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					Toast.makeText(getApplicationContext(),"SMS faild",Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (BadPaddingException e) {
					Toast.makeText(getApplicationContext(),"SMS faild",Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					Toast.makeText(getApplicationContext(),"SMS faild",Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					Toast.makeText(getApplicationContext(),"SMS faild",Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}
		});
		
		//Reset Filed
		ResetButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            	PhnNumber.setText(null);
                plainMsg.setText(null);
                encryptMsg.setText(null);

            }
        }); 
		
		//Close Running Application
		CloseButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
                System.exit(5000);
            }
        }); 
	}
		public void onResume()
		{
			super.onResume();
			
			
			smsSentReceiver = new BroadcastReceiver()
			{
				public void onReceive(Context arg0, Intent arg1)
				{
					switch(getResultCode())
					{
					case Activity.RESULT_OK:
						Toast.makeText(getBaseContext(),"SMS Sent",Toast.LENGTH_SHORT).show();
						break;
					case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						Toast.makeText(getBaseContext(),"Generic Failure",Toast.LENGTH_SHORT).show();
						break;
					case SmsManager.RESULT_ERROR_NO_SERVICE:
						Toast.makeText(getBaseContext(),"No Service",Toast.LENGTH_SHORT).show();
						break;
					case SmsManager.RESULT_ERROR_NULL_PDU:
						Toast.makeText(getBaseContext(),"Null PDU",Toast.LENGTH_SHORT).show();
						break;
					case SmsManager.RESULT_ERROR_RADIO_OFF:
						Toast.makeText(getBaseContext(),"Radio off",Toast.LENGTH_SHORT).show();
						break;
					}
				}
			};
			smsDeliveredReceiver = new BroadcastReceiver()
			{
				public void onReceive(Context arg0, Intent arg1)
				{
					switch(getResultCode())
					{
					case Activity.RESULT_OK:
						Toast.makeText(getBaseContext(),"SMS Delivered",Toast.LENGTH_SHORT).show();
						break;
					case Activity.RESULT_CANCELED:
						Toast.makeText(getBaseContext(),"SMS not Delivered",Toast.LENGTH_SHORT).show();
						break;
					}
				}
			};
			registerReceiver(smsDeliveredReceiver,new IntentFilter(DELIVERED));
			registerReceiver(smsSentReceiver, new IntentFilter(SENT));
		}
		
		public void onPause()
		{
			super.onPause();
			unregisterReceiver(smsSentReceiver);
			unregisterReceiver(smsDeliveredReceiver);
		}

		public void onSMSIntentClick(View v)
		{
		Intent i=new Intent(android.content.Intent.ACTION_VIEW);
		i.setType("vnd.android-dir/mms-sms");
		startActivity(i);
		}
		
		protected void Encrypt(String msg,String number)throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, InvalidParameterSpecException, IllegalBlockSizeException, BadPaddingException, 
		UnsupportedEncodingException, NoSuchPaddingException 
		{
		char[] password = number.toCharArray();
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec = new PBEKeySpec(password, salt, 1024, 256);
		SecretKey tmp = factory.generateSecret(spec);
		SecretKey secret = new SecretKeySpec(tmp.getEncoded(), "AES");
		/* Encrypt the message. */
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secret);
		AlgorithmParameters params = cipher.getParameters();
		@SuppressWarnings("unused")
		byte[] iv = params.getParameterSpec(IvParameterSpec.class).getIV();
		byte[] ciphertext = cipher.doFinal(msg.getBytes("UTF-8"));
		String cipher1 = new String(ciphertext);
		encryptMsg.setText(cipher1);
		} 
		

		protected void onActivityResult(int requestCode, int resultCode, Intent data) 
		{  
    	    if (resultCode == RESULT_OK) {  
    	        switch (requestCode) {  
    	        case CONTACT_PICKER_RESULT:
    	            final EditText PhnNumber = (EditText) findViewById(R.id.editText);
    	            Cursor cursor = null;  
    	            String phoneNumber = "";
    	            List<String> allNumbers = new ArrayList<String>();
    	            int phoneIdx = 0;
    	            try {  
    	                Uri result = data.getData();  
    	                String id = result.getLastPathSegment();  
    	                cursor = getContentResolver().query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + "=?", new String[] { id }, null);  
    	                phoneIdx = cursor.getColumnIndex(Phone.DATA);
    	                if (cursor.moveToFirst()) {
    	                    while (cursor.isAfterLast() == false) {
    	                        phoneNumber = cursor.getString(phoneIdx);
    	                        allNumbers.add(phoneNumber);
    	                        cursor.moveToNext();
    	                    }
    	                } else {
    	                    //no results actions
    	                }  
    	            } catch (Exception e) {  
    	               //error actions
    	            } finally {  
    	                if (cursor != null) {  
    	                    cursor.close();
    	                }
    	                final CharSequence[] items = allNumbers.toArray(new String[allNumbers.size()]);
    	                AlertDialog.Builder builder = new AlertDialog.Builder(SMSActivity.this);
    	                builder.setTitle("Choose a number");
    	                builder.setItems(items, new DialogInterface.OnClickListener() {
    	                    public void onClick(DialogInterface dialog, int item) {
    	                        String selectedNumber = items[item].toString();
    	                        selectedNumber = selectedNumber.replace("-", "");
    	                        PhnNumber.setText(selectedNumber);
    	                    }
    	                });
    	                AlertDialog alert = builder.create();
    	                if(allNumbers.size() > 1) {
    	                    alert.show();
    	                } else {
    	                    String selectedNumber = phoneNumber.toString();
    	                    selectedNumber = selectedNumber.replace("-", "");
    	                    PhnNumber.setText(selectedNumber);
    	                }

    	                if (phoneNumber.length() == 0) {  
    	                    //no numbers found actions  
    	                }  
    	            }  
    	            break;  
    	        }  
    	    } else {
    	       //activity result error actions
    	    }  
    	}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}