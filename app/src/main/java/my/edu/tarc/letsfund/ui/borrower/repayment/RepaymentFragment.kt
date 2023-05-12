package my.edu.tarc.letsfund.ui.borrower.repayment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.databinding.FragmentRepaymentBinding

class RepaymentFragment : Fragment() {
    private lateinit var composeView: ComposeView
    private var _binding: FragmentRepaymentBinding? = null

    //Color
    val light_green = Color(0xFF599D42)
    val light_green_500 = Color(0xFF8BC34A)
    val light_orange = Color(0xFFF6BD60)
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRepaymentBinding.inflate(inflater, container, false)
        val root: View = binding.root


        composeView = binding.repaymentbox
        composeView.setContent {
            Card() {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(light_orange)) {
                    Text(
                        "Repayment Amount",Modifier.padding(all = 20.dp),
                        color = Color.White, fontSize = 17.sp, fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        "RM hi",
                        Modifier
                            .padding(top = 50.dp)
                            .padding(horizontal = 19.dp),
                        fontWeight = FontWeight.Bold, color = Color.White,
                        fontSize = 22.sp, fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }


        composeView = binding.btnRepay
        composeView.setContent {
            Button(
                onClick = {

                    //findNavController().navigate(R.id.action_navigation_wallet_to_navigation_cardpayment)
                    //val intent = Intent(context, CardPaymentActivity::class.java)
                   //startActivity(intent)

                },
                contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
                colors = ButtonDefaults.outlinedButtonColors(light_green)
            ) {
                Icon(
                    painter = painterResource(R.drawable.baseline_attach_money_24),
                    contentDescription = "Localized description",
                    modifier = Modifier.size(ButtonDefaults.IconSize),
                    tint = Color.White
                )
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Repay",  color = Color.White)
            }
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}