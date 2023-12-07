package com.example.mydwindemoapp.views.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mydwindemoapp.databinding.RowItemAcMeterInfoBinding
import com.example.mydwindemoapp.models.ACMeterModel

class ACMeterListAdapter(var onItemClick: (ACMeterModel) -> Unit): ListAdapter<ACMeterModel, ACMeterListAdapter.SampleViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
        val binding = RowItemAcMeterInfoBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return SampleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class SampleViewHolder(private val binding: RowItemAcMeterInfoBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION){
                        val sampleModel = getItem(position)
                        bind(sampleModel)
                        onItemClick(sampleModel)
                    }
                }
            }
        }

        fun bind(sampleModel: ACMeterModel){
            binding.apply {
                incVoltageL1.tvLabel.text = "Voltage L1"
                incVoltageL1.tvValue.text = sampleModel.voltageL1.toString()

                incVoltageL2.tvLabel.text = "Voltage L2"
                incVoltageL2.tvValue.text = sampleModel.voltageL2.toString()

                incVoltageL3.tvLabel.text = "Voltage L3"
                incVoltageL3.tvValue.text = sampleModel.voltageL3.toString()

                incVoltageAvg.tvLabel.text = "Voltage Average"
                incVoltageAvg.tvValue.text = sampleModel.voltageAverage.toString()

                incCurrentL1.tvLabel.text = "Current L1"
                incCurrentL1.tvValue.text = sampleModel.currentL1.toString()

                incCurrentL2.tvLabel.text = "Current L2"
                incCurrentL2.tvValue.text = sampleModel.currentL2.toString()

                incCurrentL3.tvLabel.text = "Current L3"
                incCurrentL3.tvValue.text = sampleModel.currentL3.toString()

                incCurrentAvg.tvLabel.text = "Current Average"
                incCurrentAvg.tvValue.text = sampleModel.currentAverage.toString()

                incTotalKW.tvLabel.text = "Total KW"
                incTotalKW.tvValue.text = sampleModel.totalKW.toString()

                incTotalKWH.tvLabel.text = "Total KWH"
                incTotalKWH.tvValue.text = sampleModel.totalKWH.toString()

                incFrequency.tvLabel.text = "Frequency"
                incFrequency.tvValue.text = sampleModel.frequency.toString()

                incAvgPowerFactor.tvLabel.text = "Average Power Factor"
                incAvgPowerFactor.tvValue.text = sampleModel.averagePowerFactor.toString()
            }
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<ACMeterModel>(){
        override fun areItemsTheSame(oldItem: ACMeterModel, newItem: ACMeterModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ACMeterModel, newItem: ACMeterModel): Boolean {
            return oldItem == newItem
        }

    }
}