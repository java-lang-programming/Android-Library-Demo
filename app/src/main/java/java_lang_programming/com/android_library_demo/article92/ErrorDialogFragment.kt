package java_lang_programming.com.android_library_demo.article92

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java_lang_programming.com.android_library_demo.R
import kotlinx.android.synthetic.main.fragment_error_dialog.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [ErrorDialogtFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [ErrorDialogtFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ErrorDialogFragment : DialogFragment() {

    private var msg: String? = null

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            msg = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_error_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        message.text = msg ?: "error"
        btnClose.setOnClickListener({ mListener?.onClickClose() })
        btnRetry.setOnClickListener({ mListener?.onClickRetry() })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        fun onClickClose()
        fun onClickRetry()
    }

    companion object {
        const val ARG_PARAM1 = "msg"

        @JvmStatic
        fun newInstance(msg: String): ErrorDialogFragment {
            val fragment = ErrorDialogFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, msg)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
