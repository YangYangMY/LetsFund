package my.edu.tarc.letsfund.ui.lender.wallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import my.edu.tarc.letsfund.databinding.FragmentWalletBinding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import my.edu.tarc.letsfund.R

class WalletFragment : Fragment() {
    lateinit var composeView: ComposeView

    private var _binding: FragmentWalletBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    //Color
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

        val walletamount: String = "RM 500"

        composeView = binding.walletbox
        composeView.setContent {
            Card() {
                Box(Modifier.fillMaxSize().background(light_green_500)) {
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


        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}