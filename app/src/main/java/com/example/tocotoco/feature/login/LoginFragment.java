package com.example.tocotoco.feature.login;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tocotoco.R;
import com.example.tocotoco.feature.product_detail.ProductDetailActivity;
import com.example.tocotoco.feature.registerAcc.RegisterAccountActivity;
import com.example.tocotoco.home.activityhome.HomeActivity;
import com.example.tocotoco.model.LoginResult;
import com.example.tocotoco.room.TokenDevice;
import com.example.tocotoco.room.TokenDeviceDatabase;
import com.gemvietnam.base.viper.ViewFragment;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;
import retrofit2.Response;

public class LoginFragment extends ViewFragment<LoginContract.Presenter> implements LoginContract.View, View.OnClickListener {

    @BindView(R.id.tv_login)
    TextView tv_login;
    @BindView(R.id.tv_register)
    TextView tv_register;
    @BindView(R.id.ic_back)
    ImageView ic_back;
    @BindView(R.id.ed_sdt)
    TextInputEditText ed_sdt;
    @BindView(R.id.etPassword)
    TextInputEditText etPassword;
    Context context;
    SharedPreferences.Editor editor;

    private  boolean isFavorite = false;
    private  boolean isFavDetail = false;
    private  int idProductFromDetail = 0;
    Intent intent;
    public static LoginFragment getInstance() {
        return new LoginFragment();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.login_fragment;
    }

    @Override
    public void initLayout() {
        super.initLayout();
        context = getViewContext();
        setListener();
        getActionBackLogin();
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_file_key), MODE_PRIVATE);
        editor = sharedPref.edit();

    }

    private void getActionBackLogin() {
        intent = getActivity().getIntent();
        isFavorite = intent.getBooleanExtra("fromFavorite", false);
        isFavDetail = intent.getBooleanExtra("isFavProduct", false);
        idProductFromDetail = intent.getIntExtra("idProductFromDetail", 0);
    }

    private void setListener() {
        tv_login.setOnClickListener(this);
        ed_sdt.setOnClickListener(this);
        etPassword.setOnClickListener(this);
        tv_register.setOnClickListener(this);
        ic_back.setOnClickListener(this);
    }


    @Override
    public void loginSuccess(Response<LoginResult> data) {
        if (data.body() != null) {
            editor.putString(context.getString(R.string.preference_key_token), data.body().getResult());
            editor.apply();
        }
        Intent i;
        if (isFavDetail) {
            i = new Intent(getViewContext(), ProductDetailActivity.class);
            i.putExtra("goToFavoriteDetail", idProductFromDetail);
        }else {
            i = new Intent(getViewContext(), HomeActivity.class);
            if(isFavorite) {
                i.putExtra("goToFavorite", true);
            }
        }
        startActivity(i);

        Toast.makeText(getViewContext(), "Tài khoản chính xác", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_login:
                mPresenter.loginWithPass(ed_sdt.getText().toString(), etPassword.getText().toString(), "username");
                break;
            case R.id.ed_sdt:
                Log.e("tag:  ", "cldsvick");
                break;

            case R.id.etPassword:
                Log.e("tag:  ", "cdslick");
                break;
            case R.id.tv_register:
                Intent i;
                i = new Intent(getViewContext(), RegisterAccountActivity.class);
                startActivity(i);
                break;
            case R.id.ic_back:
                mPresenter.back();
                break;
        }
    }
}
