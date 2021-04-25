package com.brotherhackathon.smartlabels;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.brother.ptouch.sdk.LabelInfo;
import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener {

    private static final String TAG = "MainActivity";

    @BindView(R.id.spinner_printer_type)
    Spinner spinnerPrinterType;

    @BindView(R.id.spinner_roll_type)
    Spinner spinnerRollType;

    @BindView(R.id.checkbox_image)
    CheckBox checkboxImage;

    @BindView(R.id.checkbox_custom_message)
    CheckBox checkboxMessage;

    @BindView(R.id.tv_message)
    TextView tvMessage;

    @BindView(R.id.til_custom_msg)
    TextInputLayout tilCustomMessage;

    @BindView(R.id.et_custom_msg)
    TextInputEditText etCustomMessage;

    @BindView(R.id.btn_print)
    Button btnPrint;

    @BindView(R.id.holder_image)
    ImageView holderImage;

    @BindView(R.id.holder_layout)
    View holderLayout;


    private static final String[] PRINTERS = new String[]{
            "QL-820NWB"
    };

    private static final String[] ROLLS = new String[]{
            "29\" x 90\"",
    };

    private SharedPrefManager sharedPrefManager;
    private String            selectedImagePath;
    private boolean           isCustomMsgEnabled, isImageModeEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());

        isCustomMsgEnabled = sharedPrefManager.getCustomMsgCheckboxStatus();
        isImageModeEnabled = sharedPrefManager.getImageCheckboxStatus();

        if (isCustomMsgEnabled) {
            checkboxMessage.setChecked(true);
            tilCustomMessage.setVisibility(View.VISIBLE);
        }

        if (isImageModeEnabled) {
            checkboxImage.setChecked(true);
            holderImage.setVisibility(View.VISIBLE);
        } else {
            holderImage.setVisibility(View.GONE);
        }

        checkboxImage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPrefManager.setImageCheckBoxStatus(isChecked);
                if (isChecked) {
                    isImageModeEnabled = true;
                    holderImage.setVisibility(View.VISIBLE);
                } else {
                    isImageModeEnabled = false;
                    holderImage.setVisibility(View.GONE);
                }

            }
        });

        checkboxMessage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPrefManager.setCustomMsgCheckboxStatus(isChecked);
                if (isChecked) {
                    isCustomMsgEnabled = true;
                    tilCustomMessage.setVisibility(View.VISIBLE);
                } else {
                    isCustomMsgEnabled = false;
                    tilCustomMessage.setVisibility(View.GONE);
                }
            }
        });

        etCustomMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvMessage.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        ArrayAdapter<String> printersAdapter = new ArrayAdapter<>(
                this, R.layout.settings_spinner_dropdown, PRINTERS
        );
        printersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPrinterType.setAdapter(printersAdapter);
        spinnerPrinterType.setSelection(0, false);
        spinnerPrinterType.setOnItemSelectedListener(this);


        ArrayAdapter<String> rollsAdapter = new ArrayAdapter<>(
                this, R.layout.settings_spinner_dropdown, ROLLS
        );
        rollsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRollType.setAdapter(rollsAdapter);
        spinnerRollType.setSelection(0, false);
        spinnerRollType.setOnItemSelectedListener(this);


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;
        if (spinner.getId() == R.id.spinner_roll_type) {
            String rollType = parent.getItemAtPosition(position).toString();
            Log.d(TAG, "onItemSelected: " + rollType);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        if (requestCode == Constants.CODE_PICK_PHOTO) {
            selectedImagePath = Matisse.obtainPathResult(data).get(0);
            Glide.with(this)
                    .load(selectedImagePath)
                    .apply(RequestOptions.centerCropTransform())
                    .into(holderImage);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @OnClick(R.id.btn_info)
    void onInfoBtnClick() {
        SnackbarManager.createAnchoredSnackbar(btnPrint, "Custom label printer for mother's day.").show();
    }

    @OnClick(R.id.btn_print)
    void onPrintBtnClick() {
        String userMessage = etCustomMessage.getText().toString().trim();
        if (isCustomMsgEnabled && userMessage.length() < 1) {
            SnackbarManager.createAnchoredSnackbar(btnPrint, "Please enter a message.").show();
            return;
        }

        if (isImageModeEnabled && selectedImagePath == null) {
            SnackbarManager.createAnchoredSnackbar(btnPrint, "Please select an image.").show();
            return;
        }

        Bitmap b = BitmapUtil.getBitmapFromView(holderLayout);

        new Thread(new Runnable() {
            @Override
            public void run() {
                printMessage(b);
            }
        }).start();


    }

    @OnClick(R.id.holder_image)
    void onImageClick() {
        checkPermissions();
    }

    private void checkPermissions() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        selectPhoto();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            SnackbarManager.createAnchoredSnackbar(
                                    btnPrint,
                                    "Must enable permissions in app settings."
                            ).show();
                        } else {
                            SnackbarManager.createAnchoredSnackbar(
                                    btnPrint, "Read storage permission denied."
                            ).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void selectPhoto() {
        Matisse.from(this)
                .choose(MimeType.ofImage())
                .theme(R.style.Matisse_Dracula)
                .countable(false)
                .maxSelectable(1)
                .maxOriginalSize(10)
                .imageEngine(new GlideEngine())
                .forResult(Constants.CODE_PICK_PHOTO);
    }


    private void printMessage(Bitmap bitmap) {
        Printer myPrinter = new Printer();
        PrinterInfo myPrinterInfo = myPrinter.getPrinterInfo();
        myPrinterInfo.printerModel = PrinterInfo.Model.QL_820NWB;


        myPrinterInfo.port = PrinterInfo.Port.NET;
        myPrinterInfo.paperSize = PrinterInfo.PaperSize.CUSTOM;
        myPrinterInfo.orientation = PrinterInfo.Orientation.LANDSCAPE;
        myPrinterInfo.valign = PrinterInfo.VAlign.MIDDLE;
        myPrinterInfo.align = PrinterInfo.Align.CENTER;
        myPrinterInfo.printMode = PrinterInfo.PrintMode.FIT_TO_PAGE;
        myPrinterInfo.numberOfCopies = 1;
        myPrinterInfo.ipAddress = "192.168.118.1";

        myPrinterInfo.workPath = this.getFilesDir().getAbsolutePath() + "/";

        myPrinterInfo.labelNameIndex = LabelInfo.QL700.W29H90.ordinal();
        myPrinterInfo.isAutoCut = true;
        myPrinterInfo.isCutAtEnd = false;
        myPrinterInfo.isHalfCut = false;
        myPrinterInfo.isSpecialTape = false;

        myPrinter.setPrinterInfo(myPrinterInfo);
        myPrinter.startCommunication();
        myPrinter.printImage(bitmap);
        myPrinter.endCommunication();
    }


}
