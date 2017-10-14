package com.winhearts.arappmarket.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winhearts.arappmarket.R;
import com.winhearts.arappmarket.utils.AppManager;
import com.winhearts.arappmarket.utils.ScreenUtil;


/**
 * 更新相关的Dialog提示
 * 
 * @author hedy
 * 
 */
public class UninstallHintDialog extends Dialog {

	public static final int Type_Can_Update = 1;// 可以升级
	public static final int Type_Lastest_Version = 2;// 最新版本
	public static final int Type_Update_Loading = 3;// 升级中

	private final int mType;// 当前类型
	//private final String mInfo;// 信息hint

	private final Context mContext;
	private final LayoutParams lp;

	private TextView tvHint, tvHead;
	private Button btnUpdate, btnCancel, btnConfirm;
	private ImageView appIcon;
	private LinearLayout llOperation;
	private RelativeLayout rlConfirm;
	private String appName;
	private String packageName;
	
	public UninstallHintDialog(Context context, int type,String appName,String packageName) {
		super(context, R.style.dialog);
		// 初始
		this.mContext = context;
		this.mType = type;

		setContentView(R.layout.dialog_uninstall_hint);
		// 初始UI
		this.appName = appName;
		this.packageName = packageName;
		initView();

		// 设置window属性
		lp = getWindow().getAttributes();
		lp.gravity = Gravity.CENTER;
		// lp.dimAmount = 0; // 去背景遮盖
		// lp.alpha = 1.0f;
		// 重设宽度
		lp.width = (int) (ScreenUtil.getScreenWidth((Activity) this.mContext) / 3.0);
//		lp.height = ScreenUtil.dip2px(this.mContext, 130.0f);
		getWindow().setAttributes(lp);
		
	}

	
	/**
	 * 根据type初始化
	 */
	private void initView() {
		// 获得对应控件
		tvHead = (TextView) findViewById(R.id.app_name);
		tvHint = (TextView) findViewById(R.id.tv_hint);
		
		appIcon = (ImageView)findViewById(R.id.app_icon);
		
		Drawable drawable;
		try {
			drawable = AppManager.getAppIcon(mContext,
					packageName);
			if (drawable != null) {
				appIcon.setImageDrawable(drawable);
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		tvHead.setText(appName);
		
	//	tvHint.setText(mInfo);
		// 升级、取消
		llOperation = (LinearLayout) findViewById(R.id.ll_update_operation);
		btnUpdate = (Button) findViewById(R.id.btn_open);
		btnCancel = (Button) findViewById(R.id.btn_uninstall);
		
		
		// 确定
		rlConfirm = (RelativeLayout) findViewById(R.id.rl_confirm);
		btnConfirm = (Button) findViewById(R.id.btn_confirm);
		// 进度框

		switch (mType) {
		case Type_Can_Update:// 可以更新
			llOperation.setVisibility(View.VISIBLE);
			rlConfirm.setVisibility(View.GONE);
			break;
		case Type_Lastest_Version:// 最新版本
			tvHead.setVisibility(View.GONE);
			llOperation.setVisibility(View.GONE);
			rlConfirm.setVisibility(View.VISIBLE);
			break;
		case Type_Update_Loading:// 正在更新
			tvHead.setVisibility(View.GONE);
			llOperation.setVisibility(View.GONE);
			rlConfirm.setVisibility(View.GONE);
			break;

		default:
			break;
		}
		// 取消和确定点击后对话框消失
		View.OnClickListener onClickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				UninstallHintDialog.this.dismiss();
			}
		};
		btnCancel.setOnClickListener(onClickListener);
		btnConfirm.setOnClickListener(onClickListener);
	}

	// 升级按钮事件
	public void setUpdateButtonClick(View.OnClickListener onClickListener) {
		btnUpdate.setOnClickListener(onClickListener);
	}

	// 确定按钮事件
	public void setConfirmButtonClick(View.OnClickListener onClickListener) {
		btnConfirm.setOnClickListener(onClickListener);
	}

	// 取消按钮事件
	public void setCancelButtonClick(View.OnClickListener onClickListener) {
		btnCancel.setOnClickListener(onClickListener);
	}

	public TextView getTvHint() {
		return tvHint;
	}

	public TextView getTvHead() {
		return tvHead;
	}

	public Button getBtnUpdate() {
		return btnUpdate;
	}

	public Button getBtnCancel() {
		return btnCancel;
	}

	public Button getBtnConfirm() {
		return btnConfirm;
	}
}
