package com.example.passwordmanager

import android.app.KeyguardManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.passwordmanager.authentication.pin.DialogBuilder
import com.example.passwordmanager.databinding.ActivityMainBinding
import com.example.passwordmanager.extension.autoClearedAlertDialog
import com.example.passwordmanager.extension.autoClearedLateinit
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.android.AndroidInjection
import java.util.concurrent.Executor
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    private val refreshViewModel: RefreshViewModel by viewModels()

    private lateinit var mainViewModel: MainViewModel

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private var queryCacheAdapter by autoClearedLateinit<CacheQueryAdapter>()
    private var alertPinDialog: AlertDialog? by autoClearedAlertDialog()

    private lateinit var navController: NavController
    private var binding by autoClearedLateinit<ActivityMainBinding>()

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private lateinit var keyguardManager: KeyguardManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragmentContainer) as NavHostFragment
        navController = navHostFragment.findNavController()
        // val appBarConfiguration = AppBarConfiguration(navController.graph)
        //   binding.searchBar.setupWithNavController(navController, appBarConfiguration)
        setSupportActionBar(binding.searchBar)
        // setupActionBarWithNavController(navController, appBarConfiguration)
        mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
        mainViewModel.loadData()
        setUpListeners()
        setUpRecyclerView()
        observeViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        return true
    }

    override fun onStop() {
        super.onStop()
        mainViewModel.clearUserStatusData()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.clearUserStatusData()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.fullAccess -> {
                checkBiometric()
                Log.d("MGG3", "BIOOOOmetric")
                true
            }

            R.id.secretAccess -> {
                // navController.navigate(NavGraphDirections.mainActivityToFullAccessPinFragment())
                alertPinDialog = DialogBuilder.create(
                    this,
                    onPositiveButtonClick = { mainViewModel.checkPinForDialog(it) }
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpListeners() {
        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            val queryText = binding.searchView.text
            refreshViewModel.findItems(queryText.toString())
            binding.searchBar.setText(queryText)
            binding.searchView.hide()
            mainViewModel.loadData()
            return@setOnEditorActionListener true
        }
        binding.layoutSwipeRefresh.setOnRefreshListener {
            refreshViewModel.refresh()
        }

        binding.floatingActionButton.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        val dialogFragment = WebCredentialItemDialogFragment()
        dialogFragment.show(supportFragmentManager, "WebCredentialDialog")
    }

    private fun observeViewModel() {
        refreshViewModel.refreshingState.observe(this) { isRefreshing ->
            binding.layoutSwipeRefresh.isRefreshing = isRefreshing
        }
        mainViewModel.viewState.observe(this) {
            queryCacheAdapter.submitList(it.queryList)
        }
    }

    private fun setUpRecyclerView() {
        queryCacheAdapter = CacheQueryAdapter {
            refreshViewModel.findItems(it.query)
            binding.searchBar.setText(it.query)
            binding.searchView.hide()
            mainViewModel.loadData()
            Log.d("MGG3", "Clicked ${it.id}")
        }
        binding.cacheQueries.adapter = queryCacheAdapter
        val divider = MaterialDividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        binding.cacheQueries.addItemDecoration(divider)
    }

    private fun checkBiometric() {
        executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                    //jak się wywali, to ma to zrobić na loadingu w tle, nie pokazując zawartości i wyłączyć apkę
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(applicationContext, "Authentication succeeded!", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .apply {
                keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R && keyguardManager.isDeviceSecure) {
                    setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                } else {
                    setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                    setNegativeButtonText("Use account password")
                }
            }
            .build()
        biometricPrompt.authenticate(promptInfo)
    }

}