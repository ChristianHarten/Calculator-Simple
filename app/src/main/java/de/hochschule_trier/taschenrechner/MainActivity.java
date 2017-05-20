package de.hochschule_trier.taschenrechner;

import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class MainActivity extends AppCompatActivity {

    private TextView mCalcView;
    private Button mClearBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCalcView = (TextView) findViewById(R.id.calcView);
        mCalcView.addTextChangedListener(textAutoResizeWatcher(mCalcView, 20, 50));

        mClearBtn = (Button) findViewById(R.id.clearBtn);
        mClearBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                clearInput(v);
                return true;
            }
        });
    }

    private TextWatcher textAutoResizeWatcher(final TextView view, final int MIN_SP, final int MAX_SP) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                final int widthLimitPixels = view.getWidth() - view.getPaddingRight() - view.getPaddingLeft();
                Paint paint = new Paint();
                float fontSizeSP = pixelsToSP(view.getTextSize());
                paint.setTextSize(spToPixels(fontSizeSP));

                String viewText = view.getText().toString();

                float widthPixels = paint.measureText(viewText);

                if (widthPixels < widthLimitPixels) {
                    while (widthPixels < widthLimitPixels && fontSizeSP <= MAX_SP) {
                        ++fontSizeSP;
                        paint.setTextSize(spToPixels(fontSizeSP));
                        widthPixels = paint.measureText(viewText);
                    }
                    --fontSizeSP;
                } else {
                    while (widthPixels > widthLimitPixels || fontSizeSP > MAX_SP) {
                        if (fontSizeSP < MIN_SP) {
                            fontSizeSP = MIN_SP;
                            break;
                        }
                        --fontSizeSP;
                        paint.setTextSize(spToPixels(fontSizeSP));
                        widthPixels = paint.measureText(viewText);
                    }
                }
                view.setTextSize(fontSizeSP);
            }
        };
    }

    private float pixelsToSP(float px) {
        float scaledDens = getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDens;
    }

    private float spToPixels(float sp) {
        float scaledDens = getResources().getDisplayMetrics().scaledDensity;
        return sp * scaledDens;
    }

    public void numberButtonClick(View v) {
        String text = ((Button) v).getText().toString();
        if (text.equals("E^X")) {
            mCalcView.setText(mCalcView.getText() + "^");
            return;
        }
        mCalcView.setText(mCalcView.getText() + text);
    }

    public void clearChar(View v) {
        String text = mCalcView.getText().toString();
        if (text.length() > 0) {
            text = text.substring(0, mCalcView.getText().toString().length() - 1);
            mCalcView.setText(text);
        }
    }

    public void clearInput(View v) {
        if (mCalcView.getText().toString().length() > 0) {
            mCalcView.setText("");
        }
    }

    public void changeDMToEuro(View v) {
        String text = mCalcView.getText().toString();
        if (text.length() > 0 && !text.matches("\\D")) {
            double value = Double.parseDouble(text);
            value = value / 1.95583;
            text = String.valueOf(value);
            mCalcView.setText(text);
        }
    }

    public void calculateExpr(View v) {
        String term = mCalcView.getText().toString();
        if (term.length() > 0) {
            Expression e = new ExpressionBuilder(term).build();
            double result;
            try {
                result = e.evaluate();
            } catch (Exception exc) {
                mCalcView.setText(R.string.evaluationErrorMessage);
                return;
            }
            mCalcView.setText(String.valueOf(result));
        } else {
            mCalcView.setText(R.string.enterStuffMessage);
        }
    }

    public void scienceButtonClick(View v) {
        String source = ((Button) v).getText().toString();
        String number = mCalcView.getText().toString();

        if (number.length() > 0 && !number.matches("\\D")) {
            double value = Double.parseDouble(number);
            switch (source) {
                case "1/x":
                    if (!number.equals("0") || !number.equals("0.0")) {
                        value = 1 / value;
                    }
                    break;
                case "x^2":
                    value *= value;
                    break;
                case "\u221a":
                    if (!number.startsWith("-")) {
                        value = Math.sqrt(value);
                    } else {
                        mCalcView.setText(getResources().getString(R.string.evaluationErrorMessage));
                        return;
                    }
                    break;
                case "X!":
                    if (!number.startsWith("-")) {
                        int factorial = Integer.valueOf(number);
                        int result = 1;
                        for (int i = 1; i <= factorial; i++) {
                            result *= i;
                        }
                        value = (double) result;
                    } else {
                        mCalcView.setText(getResources().getString(R.string.evaluationErrorMessage));
                        return;
                    }
                    break;
                case "SIN":
                    value = Math.sin(value);
                    break;
                case "COS":
                    value = Math.cos(value);
                    break;
                case "TAN":
                    value = Math.tan(value);
                    break;
                case "LOG":
                    if (!number.startsWith("-")) {
                        value = Math.log(value);
                    } else {
                        mCalcView.setText(getResources().getString(R.string.evaluationErrorMessage));
                        return;
                    }
                    break;
            }
            mCalcView.setText(String.valueOf(value));
        }
    }
}

