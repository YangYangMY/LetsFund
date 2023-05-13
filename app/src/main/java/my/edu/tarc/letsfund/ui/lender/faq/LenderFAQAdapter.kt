package my.edu.tarc.letsfund.ui.lender.faq

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import my.edu.tarc.letsfund.R
import my.edu.tarc.letsfund.ui.lender.faq.Faq

class LenderFAQAdapter(private val context: Context, private val faqList: MutableList<Faq>) : BaseExpandableListAdapter() {

    override fun getGroup(groupPosition: Int): Any {
        return faqList[groupPosition]
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        val faq = getGroup(groupPosition) as Faq
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.faq_group_item_lender, null)
        }
        val faqQuestionTextView = convertView?.findViewById<TextView>(R.id.faqQuestionTextView)
        faqQuestionTextView?.text = faq.question
        return convertView!!
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return ""
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.faq_child_item_lender, null)
        }
        val faqAnswerTextView = convertView?.findViewById<TextView>(R.id.faqAnswerTextView)
        faqAnswerTextView?.text = faqList[groupPosition].answer
        return convertView!!
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return faqList.size
    }
}
