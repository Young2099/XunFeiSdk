package com.lanfeng.young.xunfeisdk.speech;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.TableRow;
import android.widget.Toast;

import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import com.iflytek.aiui.jni.AIUI;
import com.lanfeng.young.xunfeisdk.ContactRepository;
import com.lanfeng.young.xunfeisdk.DynamicEntityData;
import com.lanfeng.young.xunfeisdk.MainActivity;
import com.lanfeng.young.xunfeisdk.SoApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by yf on 2018/8/9.
 */
public class AIUIRepository {
    private static final String TAG = AIUIRepository.class.getSimpleName();
    //交互状态
    private int mCurrentState = AIUIConstant.STATE_IDLE;
    private AIUIAgent mAIUIAgent = null;
    private Context context;
    private SpeakVoice speakVoice;
    private int mAIUIState;
    //是否检测到前端点，提示 ’为说话‘ 时判断使用
    private boolean mVadBegin = false;
    private AIUIView mView;
    private ContactRepository contactRepository;
    private JSONObject mPersParams;

    public AIUIRepository(Context mainActivity) {
        this.context = mainActivity;
        contactRepository = new ContactRepository(context);

    }

    private void initContract() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Log.e(TAG, "2222222222222222 ");
                List<String> contacts = contactRepository.getContacts();
//                updateProgress(progressMsg,  "40");

//                if(contacts == null || contacts.size() == 0){
//                    mMessageViewModel.fakeAIUIResult(0, "contacts_upload", "请允许应用请求的联系人读取权限");
//                    return;
//                }

                StringBuilder contactJson = new StringBuilder();
                for (String contact : contacts) {
                    String[] nameNumber = contact.split("\\$\\$");
                    contactJson.append(String.format("{\"name\": \"%s\", \"phoneNumber\": \"%s\" }\n",
                            nameNumber[0], nameNumber[1]));
                }
//                updateProgress(progressMsg, "70");

                syncDynamicData(new DynamicEntityData(
                        "IFLYTEK.telephone_contact", "uid", "", contactJson.toString()));
                putPersParam("uid", "");
//                updateProgress(progressMsg, "100");
            }
        }.start();
    }
    public void getContract() {
        initContract();

    }
    private void syncDynamicData(DynamicEntityData data) {
        Log.e(TAG, "syncDynamicData: "+mAIUIAgent );
        try {
            // 构造动态实体数据
            JSONObject syncSchemaJson = new JSONObject();
            JSONObject paramJson = new JSONObject();

            paramJson.put("id_name", data.idName);
            paramJson.put("id_value", data.idValue);
            paramJson.put("res_name", data.resName);

            syncSchemaJson.put("param", paramJson);
            syncSchemaJson.put("data", Base64.encodeToString(
                    data.syncData.getBytes(), Base64.DEFAULT | Base64.NO_WRAP));

            // 传入的数据一定要为utf-8编码
            byte[] syncData = syncSchemaJson.toString().getBytes("utf-8");

            AIUIMessage syncAthenaMessage = new AIUIMessage(AIUIConstant.CMD_SYNC,
                    AIUIConstant.SYNC_DATA_SCHEMA, 0, "", syncData);
//            sendMessage(syncAthenaMessage);
            mAIUIAgent.sendMessage(syncAthenaMessage);
            Log.e(TAG, "syncDynamicData: /////////");
        } catch (Exception e) {
            e.printStackTrace();
//            addMessageToDB(new RawMessage(AIUI, TEXT,
//                    String.format("上传动态实体数据出错 %s", e.getMessage()).getBytes()));
            Log.e(TAG, "syncDynamicData: 上传动态实体数据出错" + e.getMessage().getBytes());
        }
    }

    //生效动态实体
    public void putPersParam(String key, String value) {
        try {
            mPersParams = new JSONObject();
            mPersParams.put(key, value);
            setPersParams(mPersParams);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置个性化(动态实体和所见即可说)生效参数
     *
     * @param persParams
     */
    public void setPersParams(JSONObject persParams) {
        try {
            //参考文档动态实体生效使用一节
            JSONObject params = new JSONObject();
            JSONObject audioParams = new JSONObject();
            audioParams.put("pers_param", persParams.toString());
            params.put("audioparams", audioParams);


            sendMessage(new AIUIMessage(AIUIConstant.CMD_SET_PARAMS, 0, 0, params.toString(), null));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startVoice() {
        mVadBegin = false;
        startRecordAudio();
    }

    private void startRecordAudio() {
        // 先发送唤醒消息，改变AIUI内部状态，只有唤醒状态才能接收语音输入
        // 默认为oneshot 模式，即一次唤醒后就进入休眠，如果语音唤醒后，需要进行文本语义，请将改段逻辑copy至startTextNlp()开头处
        if (AIUIConstant.STATE_WORKING != this.mAIUIState) {
            AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null);
            mAIUIAgent.sendMessage(wakeupMsg);
        }

        // 打开AIUI内部录音机，开始录音
        String params = "sample_rate=16000,data_type=audio";
        AIUIMessage writeMsg = new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, params, null);
        mAIUIAgent.sendMessage(writeMsg);
//        sendMessage(new AIUIMessage(AIUIConstant.CMD_START_RECORD, 0, 0, "data_type=audio,sample_rate=16000", null));
    }


    private void sendMessage(AIUIMessage message) {
        if (mAIUIAgent != null) {
            //确保AIUI处于唤醒状态
            if (mCurrentState != AIUIConstant.STATE_WORKING) {
                mAIUIAgent.sendMessage(new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null));
            }
            mAIUIAgent.sendMessage(message);
        }
    }


    /**
     * 读取配置
     */
    private String getAIUIParams() {
        String params = "";

        AssetManager assetManager = context.getResources().getAssets();
        try {
            InputStream ins = assetManager.open("cfg/aiui_phone.cfg");
            byte[] buffer = new byte[ins.available()];

            ins.read(buffer);
            ins.close();

            params = new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return params;
    }


    //AIUI事件监听器
    private AIUIListener mAIUIListener = new AIUIListener() {

        @Override
        public void onEvent(AIUIEvent event) {
            switch (event.eventType) {
                case AIUIConstant.EVENT_WAKEUP:
                    //唤醒事件
                    mVadBegin = true;
                    Log.i(TAG, "on event: " + event.eventType);
                    break;

                case AIUIConstant.EVENT_RESULT: {
                    //结果事件
                    Log.i(TAG, "on event: " + event.eventType);
                    processResult(event);
                }
                break;

                case AIUIConstant.EVENT_ERROR: {
                    //错误事件
                    Log.i(TAG, "on event: " + event.eventType);
                }
                break;

                case AIUIConstant.EVENT_VAD: {
                    //vad事件
                    if (AIUIConstant.VAD_BOS == event.arg1) {
                        mVadBegin = true;
                        //找到语音前端点
//                        showTip("找到vad_bos");
                    } else if (AIUIConstant.VAD_EOS == event.arg1) {
                        //找到语音后端点
//                        showTip("找到vad_eos");
                    } else {
//                        showTip("" + event.arg2);
                        Log.e(TAG, "onEvent: " + event.arg2);
                    }
                    if (AIUIConstant.VAD_VOL == event.arg1) {
                        Log.e(TAG, "vvvv: " + event.arg2);
                    }
                }
                break;

                case AIUIConstant.EVENT_START_RECORD: {
                    //开始录音事件
                    Log.i(TAG, "on event: " + event.eventType);
                }
                break;

                case AIUIConstant.EVENT_STOP_RECORD: {
                    //停止录音事件
                    Log.i(TAG, "on event: " + event.eventType);
                }
                break;

                case AIUIConstant.EVENT_STATE: {    // 状态事件
                    mAIUIState = event.arg1;

                    if (AIUIConstant.STATE_IDLE == mAIUIState) {
                        // 闲置状态，AIUI未开启
                    } else if (AIUIConstant.STATE_READY == mAIUIState) {
                        // AIUI已就绪，等待唤醒
                    } else if (AIUIConstant.STATE_WORKING == mAIUIState) {
                        // AIUI工作中，可进行交互
                    }
                }
                break;


                default:
                    break;
            }
        }

    };

    private void processResult(AIUIEvent event) {
        try {
            JSONObject bizParamJson = new JSONObject(event.info);
            Log.e(TAG, "processResult: " + bizParamJson.toString());
            JSONObject data = bizParamJson.getJSONArray("data").getJSONObject(0);
            JSONObject params = data.getJSONObject("params");
            JSONObject content = data.getJSONArray("content").getJSONObject(0);
            long rspTime = event.data.getLong("eos_rslt", -1);  //响应时间
            if (content.has("cnt_id")) {
                String cnt_id = content.getString("cnt_id");
                JSONObject cntJson = new JSONObject(new String(event.data.getByteArray(cnt_id), "utf-8"));

                String sub = params.optString("sub");
                JSONObject result = cntJson.optJSONObject("intent");
                if ("nlp".equals(sub) && result.length() > 2) {
                    // 解析得到语义结果
                    String str = "";
                    //在线语义结果
                    if (result.optInt("rc") == 0) {
                        JSONObject answer = result.optJSONObject("answer");
                        if (answer != null) {
                            str = answer.optString("text");
                            StringBuilder stringBuilder = null;
                            if (!TextUtils.isEmpty(str)) {
                                stringBuilder = new StringBuilder();
                                stringBuilder.append("\n");
                                stringBuilder.append(str);
                            }
                            Log.e(TAG, "result" + stringBuilder);

                            if (stringBuilder != null) {
                                speakVoice = new SpeakVoice(stringBuilder.toString(), context);
                                speakVoice.start();
                            }
                        }
                    } else {
                        str = "无法识别";
                        Toast.makeText(context, str, 0).show();
                    }

                } else if ("itrans".equals(sub)) {
                    String sid = event.data.getString("sid", "");
                    updateMessageFromItrans(sid, params, cntJson, rspTime);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private void updateMessageFromItrans(String sid, JSONObject params, JSONObject cntJson, long rspTime) {
        String text = "";

        try {
            cntJson.put("sid", sid);

            JSONObject transResult = cntJson.optJSONObject("trans_result");
            if (transResult != null && transResult.length() != 0) {
                text = transResult.optString("dst");
            }

            if (TextUtils.isEmpty(text)) {
                return;
            }

            int rstId = params.optInt("rstid");
            if (rstId == 1) {
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(cntJson);
                anlaysize(jsonArray.toString().getBytes());
            } else {
//                //追加模式
//                JSONArray jsonArray = new JSONArray(new String(mAppendTransMsg.msgData));
//                jsonArray.put(cntJson);
//                mAppendTransMsg.cacheContent = mAppendTransMsg.cacheContent + "\n"  + text;
//                mAppendTransMsg.msgData = jsonArray.toString().getBytes();
//                updateMessage(mAppendTransMsg);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        boolean lrst = params.optString("lrst").equals("1");
//        if(mAppendTransMsg == null){
//            mAppendTransMsg = new RawMessage(AIUI, TEXT,
//                    fakeSemanticResult(0, "translation", text, null, null).getBytes(),
//                    text, System.currentTimeMillis(), rspTime);
//            addMessageToDB(mAppendTransMsg);
//        }else{
//            mAppendTransMsg.cacheContent = mAppendTransMsg.cacheContent + text;
//            updateMessage(mAppendTransMsg);
//            if(lrst){
//                mAppendTransMsg = null;
//            }
//        }
    }

    private void anlaysize(byte[] bytes) {

    }


    public void attach(AIUIView mView) {
        this.mView = mView;
    }

    public void detachView() {
        mView = null;
        stopRecordAudio();
    }

    public void stopAudio() {
        if (!mVadBegin) {
            Toast.makeText(context, "没有语音消息", 0).show();
        }
        sendMessage(new AIUIMessage(AIUIConstant.CMD_STOP_RECORD, 0, 0, "data_type=audio,sample_rate=16000", null));

    }

    private void stopRecordAudio() {
        if (null != mAIUIAgent) {
            sendMessage(new AIUIMessage(AIUIConstant.CMD_STOP, 0, 0, null, null));
            mAIUIAgent.destroy();
        }
    }

    public void initAIUIAgent() {
        if (null == mAIUIAgent) {
            Log.i(TAG, "create aiui agent");
            //创建AIUIAgent
            mAIUIAgent = AIUIAgent.createAgent(context, getAIUIParams(), mAIUIListener);
        }

        if (null == mAIUIAgent) {
            final String strErrorTip = "创建 AIUI失败！";
            Toast.makeText(context, strErrorTip, 0).show();
        }
    }

}
