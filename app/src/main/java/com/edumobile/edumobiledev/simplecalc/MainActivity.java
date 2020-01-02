package com.edumobile.edumobiledev.simplecalc;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

    // constants for binary operations:
    private static final int opNone = 0x00;
    private static final int opAdd = 0x01 << 0;
    private static final int opSubtract = 0x01 << 1;
    private static final int opMultiply = 0x01 << 2;
    private static final int opDivide = 0x01 << 3;

    // the display:
    private TextView tvDisplay;

    // flags to maintain state:
    private boolean enteringDigits = false;
    private boolean decimalPointAllowed = true;

    // values for calculation:
    private double result = 0.0;
    private double lastNumberEntered = 0.0;

    // the pending operation to be performed:
    private int pendingOperation = opNone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find the display textview:
        tvDisplay = (TextView) findViewById(R.id.tvDisplay);
    }

    public void digitPressed(View view) {

        // cast this view to a Button:
        Button btn = (Button) view;
        // and get its text as a string:
        String digit = (String) btn.getText();

        // is the user already entering digits?
        if (enteringDigits) {
            if (digit.equals(".")) {
                //only allow one decimal point in a single number:
                if (decimalPointAllowed) {
                    // add the . after the current display string:
                    tvDisplay.setText(String.format("%s.", tvDisplay.getText()));
                    decimalPointAllowed = false;
                }
            } else {
                //not a decimal point, add the new digit to the diplay string:
                tvDisplay.setText(String.format("%s%s", tvDisplay.getText(), digit));
            }
        } else {
            // this is the first digit entered in this number, set the display:
            tvDisplay.setText(digit);
            // we're now entering digits:
            enteringDigits = true;
        }
    }

    public void operationPressed(View view) {

        // we're no longer entering digits, and the next number may
        // contain a decimal point:
        enteringDigits = false;
        decimalPointAllowed = true;

        // the current display string as a double:
        lastNumberEntered = Double.parseDouble((String) tvDisplay.getText());

        // get this view as a Button, and get its text as a CharSequence:
        Button btn = (Button) view;
        CharSequence opString = btn.getText();

        // this is the first character of the button text:
        char operation = opString.charAt(0);

        // if there is a pending operation, calculate:
        if (pendingOperation != opNone) {
            calculate();
        } else {
            // else, set the result to the last number entered:
            result = lastNumberEntered;
        }

        switch (operation) {
            case 'A':   //all clear
                result = 0.0;
                pendingOperation = opNone;
            case 'C':   //clear (all clear falls through to this):
                tvDisplay.setText("0");
                break;
            case '+':   //add or negate
                if (opString.length() > 1) {    //negate
                    result = -result;
                    tvDisplay.setText(String.format("%f", result));
                } else {                        //add
                    pendingOperation = opAdd;
                }
                break;
            case '-':   //subtract
                pendingOperation = opSubtract;
                break;
            case '*':   //multiply
                pendingOperation = opMultiply;
                break;
            case '/':   //divide
                pendingOperation = opDivide;
                break;
            case '=':    //equals
                pendingOperation = opNone;
                break;
            default:
                break;
        }
    }

    private void calculate() {
        // get the pending operation, and calculate accordingly:
        switch (pendingOperation) {
            case opAdd:
                result = result + lastNumberEntered;
                break;
            case opSubtract:
                result = result - lastNumberEntered;
                break;
            case opMultiply:
                result = result * lastNumberEntered;
                break;
            case opDivide:
                result = result / lastNumberEntered;
                break;
            default:
                Log.e("calc", "ERROR: Attempted unknown operation in method \"calculate\"");
        }

        // output the result of the calculation to the display as a string:
        tvDisplay.setText(String.format("%f", result));

    }
}
