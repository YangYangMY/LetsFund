package my.edu.tarc.letsfund.ui.payment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import my.edu.tarc.letsfund.databinding.FragmentCardPaymentBinding
import java.util.Calendar


class CardPaymentFragment : Fragment() {

    private var _binding: FragmentCardPaymentBinding? = null
    private val binding get() = _binding!!

    private var current = ""
    private val mmyyyy = "MMYYYY"
    private val cal: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCardPaymentBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Check the textField changes status
        focusListener()

        //Exp Date input TextChanged
        binding.editTextCardExpDate.addTextChangedListener(textWatcher)


        //Proceed Payment
        binding.btnPay?.setOnClickListener {
            submitForm()
        }

        //Reset Card
        binding.btnReset.setOnClickListener {
            resetCardInput()
        }

    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (!s.toString().equals(current)) {
                var clean = s.toString().replace("[^\\d.]".toRegex(), "")
                val cleanC = current.replace("[^\\d.]".toRegex(), "")

                val cl = clean.length
                var sel = cl
                for (i in 2..cl step 2) {
                    sel++
                }
                //Fix for pressing delete next to a forward slash
                if (clean == cleanC) sel--

                if (clean.length < 6) {
                    clean += mmyyyy.substring(clean.length)
                } else {
                    //This part makes sure that when we finish entering numbers
                    //the date is correct, fixing it otherwise
                    val mon = clean.substring(0, 2).toInt()
                    var year = clean.substring(2, 6).toInt()

                    cal.set(Calendar.MONTH, mon - 1)
                    cal.set(Calendar.YEAR, year)

                    val maxMonth = cal.getActualMaximum(Calendar.MONTH) + 1

                    clean = if (mon > maxMonth) {
                        String.format("%02d%02d", maxMonth, year)
                    } else {
                        String.format("%02d%02d", mon, year)
                    }

                }

                clean = String.format("%s/%s", clean.substring(0, 2),
                    clean.substring(2, 6))

                sel = if (sel < 0) 0 else sel
                current = clean
                binding.editTextCardExpDate.setText(current)
                binding.editTextCardExpDate.setSelection(if (sel < current.length) sel else current.length)
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun afterTextChanged(s: Editable) {}
    }

    private fun submitForm() {

        //Output of helperText
        binding.cardHolderNameContainer.helperText = validCardHolderName()
        binding.cardNumberContainer.helperText = validCardNumber()
        binding.cardExpDateContainer.helperText = validCardExpDate()
        binding.cvvContainer.helperText = validCVV()

        //Check if the helperText is null
        val validCardHolder = binding.cardHolderNameContainer.helperText == null
        val validCardNumber = binding.cardNumberContainer.helperText == null
        val validCardExpDate = binding.cardExpDateContainer.helperText == null
        val validCardCVV = binding.cvvContainer.helperText == null

        if (validCardHolder && validCardNumber && validCardExpDate && validCardCVV) {
            Toast.makeText(context, "Payment Successfully", Toast.LENGTH_SHORT).show()
        }else {
            Toast.makeText(context, "Payment Failed. Please try again", Toast.LENGTH_SHORT).show()
        }

    }

    private fun focusListener() {

        //Card Holder Name
        binding.editTextCardHolderName?.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                binding.cardHolderNameContainer?.helperText = validCardHolderName()
            }
        }

        //Card Number
        binding.editTextCardNumber?.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                binding.cardNumberContainer?.helperText = validCardNumber()
            }
        }

        //Card Expire Date
        binding.editTextCardExpDate?.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                binding.cardExpDateContainer?.helperText = validCardExpDate()
            }
        }

        //Card CVV
        binding.editTextCVV?.setOnFocusChangeListener { _, focused ->
            if(!focused) {
                binding.cvvContainer?.helperText = validCVV()
            }
        }


    }

    private fun validCardHolderName(): String? {
        val cardHolderNameText = binding.editTextCardHolderName?.text.toString()
        if (cardHolderNameText.isEmpty()) {
            return "Required"
        }
        return null
    }

    private fun validCardNumber(): String? {
        val cardNumberText = binding.editTextCardNumber?.text.toString()
        if (cardNumberText.isEmpty()) {
            return "Required"
        }
        if (!cardNumberText.matches(".*[0-9].*".toRegex())) {
            return "Must be all Digits"
        }
        if(!cardNumberText.startsWith("4")) {
            return "The card number must start with '4' digit"
        }
        if(cardNumberText.length != 16) {
            return "Must be 16 Digits"
        }
        return null
    }

    private fun validCardExpDate(): String? {
        val expDateText = binding.editTextCardExpDate?.text.toString()
        if (expDateText.isEmpty()) {
            return "Required"
        }
        if (!expDateText.substring(0, 2).matches(Regex("\\d+"))) {
            return "Expire Date must be all digits"
        }
        if (!expDateText.substring(3, 7).matches(Regex("\\d+"))) {
            return "Expire Date must be all digits"
        }
        return null
    }

    private fun validCVV(): String? {
        val cvvText = binding.editTextCVV?.text.toString()
        if (cvvText.isEmpty()) {
            return "Required"
        }
        if (!cvvText.matches(".*[0-9].*".toRegex())) {
            return "Must be all Digits"
        }
        if(cvvText.length != 3) {
            return "Must be 3 Digits"
        }
        return null
    }

    private fun resetCardInput() {

        //Reset Edit Text
        binding.editTextCardHolderName.setText("")
        binding.editTextCardNumber.setText("")
        binding.editTextCardExpDate.setText("")
        binding.editTextCVV.setText("")

        //Reset HelperText
        binding.cardHolderNameContainer.helperText = ""
        binding.cardNumberContainer.helperText = ""
        binding.cardExpDateContainer.helperText = ""
        binding.cvvContainer.helperText = ""

    }


}