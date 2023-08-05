package com.reader.mlkit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import com.reader.mlkit.databinding.FragmentFirstBinding

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners(){
        binding.qrCodeScan.setOnClickListener {
            scanQrCode()
        }

        binding.barCodeScan.setOnClickListener {
            scanBarCode()
        }

        binding.imageRecognition.setOnClickListener {
            recognizeImage()
        }
    }

    private fun checkForModelDownload(scannerType: String = "qr"){
        val optionalModuleApi = GmsBarcodeScanning.getClient(requireContext())
        val moduleInstallClient = ModuleInstall.getClient(requireContext())
        moduleInstallClient
            .areModulesAvailable(optionalModuleApi)
            .addOnSuccessListener {
                if (it.areModulesAvailable()) {
                    if(scannerType.equals("qr", true)){
                        scanQrCode()
                    } else if (scannerType.equals("barcode", true)){
                        scanBarCode()
                    } else {
                        recognizeImage()
                    }
                } else {
                    // Modules are not present on the device so need to download here
                    val moduleInstallRequest =
                        ModuleInstallRequest.newBuilder()
                            .addApi(optionalModuleApi)
                            .build()

                    moduleInstallClient
                        .installModules(moduleInstallRequest)
                        .addOnSuccessListener {
                            if(scannerType.equals("qr", true)){
                                scanQrCode()
                            } else if (scannerType.equals("barcode", true)){
                                scanBarCode()
                            } else {
                                recognizeImage()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Module Installation Failed QR: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed QR: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun scanQrCode(){
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom()
            .build()
        val scanner = GmsBarcodeScanning.getClient(requireContext(), options)

        scanner.startScan()
            .addOnSuccessListener {
                Toast.makeText(context, "Success QR: ${it.rawValue}", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed QR: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun scanBarCode(){

    }

    private fun recognizeImage(){

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}