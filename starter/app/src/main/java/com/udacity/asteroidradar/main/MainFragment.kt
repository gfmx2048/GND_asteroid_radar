package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.adapters.AsteroidAdapter
import com.udacity.asteroidradar.databinding.FragmentMainBinding
import timber.log.Timber

class MainFragment : Fragment() {

    /**
     * Use "by viewModels" when the activity/fragment creating the VM will be the only activity/fragment accessing the VM’s data.
     *
     * Use "by activityViewModels" in fragments, when the fragment is sharing data/communicating with another activity/fragment.
     *
     * Use "by navGraphViewModels(R.id.some_nav_graph)" in a fragment where the VM benefits by being scoped to a specific navigation graph or nested graph.
     */
    private val mViewModel: MainViewModel by activityViewModels { MainViewModel.MainViewModelFactory(requireActivity().application) }

    private lateinit var mBinding: FragmentMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        mBinding = FragmentMainBinding.inflate(inflater)
        mBinding.lifecycleOwner = this

        mBinding.viewModel = mViewModel

        setHasOptionsMenu(true)
        subscribeToLiveData()
        return mBinding.root
    }

    private fun subscribeToLiveData() {
        mViewModel.error.observe(viewLifecycleOwner, Observer {
            it?.let {
                Timber.d(it)
                mViewModel.clearErrorResponse()
            }
        })

        mViewModel.asteroids.observe(viewLifecycleOwner, {
            it?.let {
                mBinding.asteroidRecycler.apply {
                    val asteroidAdapter = AsteroidAdapter()
                    adapter = asteroidAdapter
                    asteroidAdapter.mData = it
                }
                mBinding.asteroidRecycler.visibility = View.VISIBLE
                mViewModel.clearAsteroidsResponse()
            }
        })

        mViewModel.pictureOfTheDAy.observe(viewLifecycleOwner, {
            it?.let {
                if(it.mediaType.equals("video",true)) {
                    mBinding.activityMainImageOfTheDay.visibility = View.GONE
                    mBinding.activityMainVideoOfTheDay.visibility = View.VISIBLE
                    mBinding.activityMainVideoOfTheDay.apply {
                        setVideoPath(it.url)
                        start()
                    }

                }else{
                    mBinding.activityMainImageOfTheDay.visibility = View.VISIBLE
                    mBinding.activityMainVideoOfTheDay.visibility = View.GONE
                    Picasso.with(requireContext()).load(it.url).into(mBinding.activityMainImageOfTheDay)
                }
                mBinding.textView.text = it.title
                mViewModel.clearPictureOfTheDayResponse()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.show_all_menu ->{

            }
            R.id.show_today_menu ->{

            }
            R.id.show_saved_menu ->{

            }
        }
        return true
    }
}
