package ru.movika.test.ui

import android.animation.ObjectAnimator
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.VideoView
import androidx.fragment.app.Fragment
import ru.movika.test.R
import ru.movika.test.databinding.FragmentVideoBinding


class VideoFragment : Fragment() {

    private var _binding: FragmentVideoBinding? = null
    private val binding get() = _binding!!
    private var isAnimationStart = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        val info = binding.info
        val timerLeft = binding.timerTextViewLeft
        val timerRight = binding.timerTextViewRight
        val videoView = binding.videoView
        val tapButton = binding.tapButton
        val tryAgain = binding.tryAgain

        val uriPreLaunch = Uri.parse("android.resource://" + context?.packageName.toString() + "/" + R.raw.pre_launch)
        val uriFailedLaunch = Uri.parse("android.resource://" + context?.packageName.toString() + "/" + R.raw.failed_launch)
        val uriLaunch = Uri.parse("android.resource://" + context?.packageName.toString() + "/" + R.raw.launch)

        startVideo(videoView, uriPreLaunch)
        val timer = setInteractionTimer(videoView, uriPreLaunch, uriFailedLaunch, binding)
        timer.start()

        tapButton.setOnClickListener {
            timer.cancel()
            tapButton.visibility = View.GONE
            info.text = getString(R.string.launch_success_label)
            info.visibility = View.VISIBLE
            timerLeft.visibility = View.GONE
            timerRight.visibility = View.GONE
            tryAgain.visibility = View.VISIBLE
            startVideo(videoView, uriLaunch)
            isAnimationStart = false
            timerLeft.setTextColor(Color.BLACK)
            timerRight.setTextColor(Color.BLACK)
        }

        tryAgain.setOnClickListener {
            tryAgain.visibility = View.GONE
            info.visibility = View.GONE
            startVideo(videoView, uriPreLaunch)
            timer.start()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getVideoDuration(uri: Uri): Long {
        val mediaMetadataRetriever = MediaMetadataRetriever()
        mediaMetadataRetriever.setDataSource(context, uri)
        val duration =
            mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong()
        mediaMetadataRetriever.release()

        return duration as Long
    }

    private fun startVideo(videoView: VideoView, uri: Uri) {
        videoView.stopPlayback()
        videoView.setVideoURI(uri)
        videoView.start()
    }

    private fun setInteractionTimer(videoView: VideoView, preLaunchVideoUri: Uri,
                                    failedLaunchVideoUri: Uri,
                                    binding: FragmentVideoBinding): CountDownTimer{
        val videoDuration = getVideoDuration(preLaunchVideoUri)

        val timer = object: CountDownTimer(videoDuration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished < 5000) {
                    val timeLeft = (millisUntilFinished/1000 + 1).toString()
                    binding.timerTextViewLeft.visibility = View.VISIBLE
                    binding.timerTextViewRight.visibility = View.VISIBLE
                    binding.tapButton.visibility = View.VISIBLE
                    binding.timerTextViewLeft.text = timeLeft
                    binding.timerTextViewRight.text = timeLeft
                    if (!isAnimationStart) {
                        startButtonAnimation(binding.tapButton, binding.videoView)
                        isAnimationStart = true
                    }
                }
                if (millisUntilFinished < 3000) {
                    binding.timerTextViewLeft.setTextColor(Color.RED)
                    binding.timerTextViewRight.setTextColor(Color.RED)
                }
            }

            override fun onFinish() {
                binding.tapButton.visibility = View.GONE
                binding.info.text = getString(R.string.launch_failed_label)
                binding.info.visibility = View.VISIBLE
                binding.tryAgain.visibility = View.VISIBLE
                binding.timerTextViewLeft.visibility = View.GONE
                binding.timerTextViewRight.visibility = View.GONE
                startVideo(videoView, failedLaunchVideoUri)
                isAnimationStart = false
                binding.timerTextViewLeft.setTextColor(Color.BLACK)
                binding.timerTextViewRight.setTextColor(Color.BLACK)
            }
        }
        return timer
    }

    private fun startButtonAnimation(button: ImageButton, videoView: VideoView) {
        val animationTime = 1500L
        val animationMarginTop = 256f

        val objectAnimator = ObjectAnimator.ofFloat(button, "translationY",
                                            0f, -videoView.height.toFloat() + animationMarginTop)
        objectAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            button.translationY = value
        }
        objectAnimator.repeatMode = ObjectAnimator.REVERSE
        objectAnimator.repeatCount = 5
        objectAnimator.duration = animationTime
        objectAnimator.start()
    }
}
