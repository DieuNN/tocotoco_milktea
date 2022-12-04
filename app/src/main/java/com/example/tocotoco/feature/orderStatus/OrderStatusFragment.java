package com.example.tocotoco.feature.orderStatus;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tocotoco.MyFirebaseMessagingService;
import com.example.tocotoco.R;
import com.example.tocotoco.dialog.DialogUtils;
import com.example.tocotoco.feature.order.ConfirmSuccessOrderActivty;
import com.example.tocotoco.feature.order.OrderAdapter;
import com.example.tocotoco.feature.registerAcc.RegisterAccountContract;
import com.example.tocotoco.feature.registerAcc.RegisterAccountFragment;
import com.example.tocotoco.home.activityhome.HomeActivity;
import com.example.tocotoco.model.ProductSessionModel;
import com.example.tocotoco.model.ProductsResult;
import com.example.tocotoco.model.RegisterResult;
import com.example.tocotoco.model.UserCurrentResult;
import com.example.tocotoco.util.TypefaceNew;
import com.gemvietnam.base.viper.ViewFragment;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Response;

public class OrderStatusFragment extends ViewFragment<OrderStatusContract.Presenter> implements OrderStatusContract.View, View.OnClickListener{
    @BindView(R.id.tv_start_price)
    TextView tv_start_price;
    @BindView(R.id.tv_end_price)
    TextView tv_end_price;
    @BindView(R.id.tv_order_status)
    TextView tv_order_status;
    @BindView(R.id.btn_destroy)
    TextView btn_destroy;
    @BindView(R.id.btn_confirm)
    Button btn_confirm;
    @BindView(R.id.btn_go_home)
    Button btn_go_home;
    @BindView(R.id.rcv_order)
    RecyclerView rcv_order;
    @BindView(R.id.gifview)
    GifImageView gifview;
    @BindView(R.id.tv_status_1)
    TextView tv_status_1;
    @BindView(R.id.tv_status_2)
    TextView tv_status_2;
    @BindView(R.id.tv_status_3)
    TextView tv_status_3;
    @BindView(R.id.tv_quan)
    TextView tv_quan;
    private String token;
    private Intent intent;
    boolean isShipping;
    SharedPreferences sharedPref;
    ItemsOrderAdapter itemsOrderAdapter;
    List<ProductsResult.ProductsResultModel> list;
    DecimalFormat formatter = new DecimalFormat("#,###,###");
    public static OrderStatusFragment getInstance() {
        return new OrderStatusFragment();
    }

    @Override
    public void initLayout() {
        super.initLayout();
        setListener();
        initData();
    }


    private void setListener() {
        btn_confirm.setOnClickListener(this);
        btn_go_home.setOnClickListener(this);
    }

    private void initData() {
        intent = getViewContext().getIntent();
        isShipping = intent.getBooleanExtra("shipping", false);

        sharedPref = getViewContext().getSharedPreferences(requireContext().getString(R.string.preference_file_key), MODE_PRIVATE);
        token = sharedPref.getString(requireContext().getString(R.string.preference_key_token), "");
        mPresenter.getUserCurrentOrder(token);
        tv_start_price.setPaintFlags(tv_start_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_confirm:
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + "18008168"));
                if (ContextCompat.checkSelfPermission(getViewContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(getViewContext(), new String[]{Manifest.permission.CALL_PHONE}, 1);
                else
                    startActivity(intent);
                break;
            case R.id.btn_go_home:
                Intent i = new Intent(getViewContext(), HomeActivity.class);
                startActivity(i);
                getViewContext().finish();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.order_status_fragment;
    }

    @Override
    public void getUserCurrentOrderSuccess(UserCurrentResult userCurrentResult) {
        tv_end_price.setText(formatter.format(Integer.parseInt(userCurrentResult.getResults().getTotal())) + "đ");
        mPresenter.getItemsInOrder(token, userCurrentResult.getResults().getOrderId());
    }


    @Override
    public void getItemsInOrderSuccess(ProductsResult productsResult) {
        int count = 0;
        for (int i = 0; i < productsResult.getResults().size(); i++) {
            count += productsResult.getResults().get(i).getQuantity();
        }
        tv_quan.setText("Tổng cộng(" + count + " món)");

        list = productsResult.getResults();
        itemsOrderAdapter = new ItemsOrderAdapter(getViewContext(), list);
        rcv_order.setAdapter(itemsOrderAdapter);
        rcv_order.setLayoutManager(new LinearLayoutManager(getViewContext()));
    }

    @Override
    public void updateUIShipping(String shipping) {
        gifview.setImageResource(R.drawable.ship);
        tv_order_status.setText("Đơn hàng của bạn đang được giao");
        btn_destroy.setVisibility(View.GONE);
        tv_status_1.setBackgroundResource(0);
        tv_status_1.setTypeface(TypefaceNew.getTypefaceSFProTextRegular(getViewContext()));
        tv_status_1.setTextColor(ContextCompat.getColor(getViewContext(), R.color.color_44494D));
        tv_status_2.setBackgroundResource(R.drawable.custom_buttom);
        tv_status_2.setTypeface(TypefaceNew.getTypefaceSFProTextBold(getViewContext()));
        tv_status_2.setTextColor(ContextCompat.getColor(getViewContext(), R.color.white));
        ViewGroup.LayoutParams params = gifview.getLayoutParams();
        params.height = 450;
        params.width = 450;
        gifview.setLayoutParams(params);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DialogUtils.dismissProgressDialog();
            }
        }, 300);
    }

    @Override
    public void finishOrder(String finishOrder) {
        btn_go_home.setVisibility(View.VISIBLE);
        gifview.setImageResource(R.drawable.ic_success_order);
        tv_order_status.setText(finishOrder);
        btn_destroy.setVisibility(View.GONE);
        tv_status_2.setBackgroundResource(0);
        tv_status_2.setTypeface(TypefaceNew.getTypefaceSFProTextRegular(getViewContext()));
        tv_status_2.setTextColor(ContextCompat.getColor(getViewContext(), R.color.color_44494D));
        tv_status_3.setBackgroundResource(R.drawable.custom_buttom);
        tv_status_3.setTypeface(TypefaceNew.getTypefaceSFProTextBold(getViewContext()));
        tv_status_3.setTextColor(ContextCompat.getColor(getViewContext(), R.color.white));
        ViewGroup.LayoutParams params = gifview.getLayoutParams();
        params.height = 550;
        params.width = 550;
        gifview.setLayoutParams(params);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                DialogUtils.dismissProgressDialog();
            }
        }, 300);
    }
}
