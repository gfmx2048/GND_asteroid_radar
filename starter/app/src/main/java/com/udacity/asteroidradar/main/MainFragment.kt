package com.udacity.asteroidradar.main

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.adapters.AsteroidAdapter
import com.udacity.asteroidradar.adapters.AsteroidListener
import com.udacity.asteroidradar.data.AsteroidsFilter
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
    private val mViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory(
            requireActivity().application
        )
    }

    private lateinit var mBinding: FragmentMainBinding
    private lateinit var mAdapter: AsteroidAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mBinding = FragmentMainBinding.inflate(inflater)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mViewModel

        mAdapter = AsteroidAdapter(AsteroidListener { mViewModel.onAsteroidClicked(it) })
        mBinding.asteroidRecycler.adapter = mAdapter

        setHasOptionsMenu(true)
        subscribeToLiveData()
        return mBinding.root
    }

    private fun subscribeToLiveData() {
        mViewModel.error.observe(viewLifecycleOwner, {
            it?.let {
                Timber.d(it)
                mViewModel.clearErrorResponse()
            }
        })

        mViewModel.asteroids.observe(viewLifecycleOwner, {
            it?.let {
                mBinding.asteroidRecycler.visibility = View.VISIBLE
                mAdapter.submitList(it)
            }
        })

        mViewModel.selectedAsteroid.observe(viewLifecycleOwner, {
            it?.let {
                //We could make details fragment to implement mainViewModel so it could listen to the selected Asteroid immediately, but i will use safe Args here because it's already added to the navigation
                mViewModel.clearSelectedAsteroid()
                this.findNavController().navigate(MainFragmentDirections.actionShowDetail(it))
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_overflow_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        mViewModel.updateFilter(
            when (item.itemId) {
                R.id.show_week_menu -> AsteroidsFilter.SHOW_WEEK
                R.id.show_today_menu -> AsteroidsFilter.SHOW_TODAY
                else -> AsteroidsFilter.SHOW_SAVED
            }
        )

        return true
    }
}
