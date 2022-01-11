package app.gigg.me.app.Activity.freelance.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.craftman.cardform.Card;
import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnCalcelBtnClickListener;
import com.craftman.cardform.OnPayBtnClickListner;

import org.jetbrains.annotations.NotNull;

import app.gigg.me.app.R;
import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.Transaction;
import co.paystack.android.model.Charge;

public class CardFragment extends DialogFragment {
    private String amount;
    private String email;
    private ProgressBar progressBar;
    private CardForm cardForm;
    private CardFragmentInterface cardFragmentInterface;

    public CardFragment() {
        // Required empty public constructor
    }

    public CardFragment(String amount, String email, CardFragmentInterface cardFragmentInterface) {
        this.amount = amount;
        this.email = email;
        this.cardFragmentInterface = cardFragmentInterface;
    }


    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progress_bar_pay);
        cardForm = view.findViewById(R.id.card_form);
        cardForm.setAmount(amount);
        cardForm.setPayBtnClickListner(new OnPayBtnClickListner() {
            @Override
            public void onClick(Card card) {
                progressBar.setVisibility(View.VISIBLE);
                cardForm.setVisibility(View.GONE);
                validateCard(card.getNumber(), card.getExpMonth(), card.getExpYear(), card.getCVC());
            }
        });

        cardForm.setCancelBtnCLickListener(new OnCalcelBtnClickListener() {
            @Override
            public void onClick() {
                dismiss();
            }
        });
    }

    private void validateCard(String number, Integer expMonth, Integer expYear, String cvc) {
        co.paystack.android.model.Card card = new co.paystack.android.model.Card(number, expMonth, expYear, cvc);
        if (card.isValid()) {
            Charge charge = new Charge();
            charge.setAmount((int) Double.parseDouble(amount) * 100);
            charge.setEmail(email);
            charge.setCurrency("USD");
            charge.setCard(card);

            PaystackSdk.chargeCard(getActivity(), charge, new Paystack.TransactionCallback() {
                @Override
                public void onSuccess(Transaction transaction) {
                    cardFragmentInterface.paymentSuccess();
                    progressBar.setVisibility(View.GONE);
                    cardForm.setVisibility(View.VISIBLE);
                    dismiss();
                }

                @Override
                public void beforeValidate(Transaction transaction) {
                    // This is called only before requesting OTP.
                    // Save reference so you may send to server. If
                    // error occurs with OTP, you should still verify on server.

                }

                @Override
                public void onError(Throwable error, Transaction transaction) {
                    cardFragmentInterface.paymentFailed();
                    progressBar.setVisibility(View.GONE);
                    cardForm.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

            });
        } else {
            progressBar.setVisibility(View.GONE);
            cardForm.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Invalid", Toast.LENGTH_SHORT).show();
        }
    }

    public interface CardFragmentInterface {
        void paymentSuccess();

        void paymentFailed();
    }

    @Override
    public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
        super.onDismiss(dialog);
        cardFragmentInterface.paymentFailed();
    }
}