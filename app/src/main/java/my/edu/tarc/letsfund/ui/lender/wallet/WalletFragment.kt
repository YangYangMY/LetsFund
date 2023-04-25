package my.edu.tarc.letsfund.ui.lender.wallet

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import my.edu.tarc.letsfund.databinding.FragmentWalletBinding
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.edu.tarc.letsfund.R

class WalletFragment : Fragment() {
    private lateinit var composeView: ComposeView

    private var _binding: FragmentWalletBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //Color
    val light_green = Color(0xFF599D42)
    val light_green_500 = Color(0xFF8BC34A)
    val light_orange = Color(0xFFF6BD60)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val walletViewModel =
            ViewModelProvider(this).get(WalletViewModel::class.java)

        _binding = FragmentWalletBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val textView: TextView? = null
        walletViewModel.text.observe(viewLifecycleOwner) {
            textView?.text = it
        }

        val walletamount: String = "RM 500.00"

        composeView = binding.walletbox
        composeView.setContent {
            Card() {
                Box(Modifier.fillMaxSize().background(light_orange)) {
                    Text(
                        "LetsFund Wallet",Modifier.padding(all = 20.dp),
                        color = Color.White, fontSize = 17.sp, fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        walletamount, Modifier.padding(top = 50.dp).padding(horizontal = 19.dp),
                        fontWeight = FontWeight.Bold, color = Color.White,
                        fontSize = 22.sp, fontFamily = FontFamily.SansSerif
                    )
                }
            }
        }

        composeView = binding.TopUp
        composeView.setContent {
            Button(
                onClick = { /* Do something! */ },
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
                Text("Top Up",  color = Color.White)
            }
        }

        composeView = binding.Withdraw
        composeView.setContent {
            Button(
                onClick = { /* Do something! */ },
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
                Text("Withdraw",  color = Color.White)
            }
        }

        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}