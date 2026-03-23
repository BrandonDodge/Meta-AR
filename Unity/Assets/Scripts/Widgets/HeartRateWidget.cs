using UnityEngine;

namespace HUDLink.Widgets
{
    /// <summary>
    /// Event payload specifically for Heart Rate data drops from the Android transport.
    /// </summary>
    public class HeartRateEvent : WidgetEvent
    {
        public int BPM { get; private set; }
        public float Confidence { get; private set; }

        public HeartRateEvent(int version, int bpm, float confidence) : base(version)
        {
            BPM = bpm;
            Confidence = confidence;
        }
    }

    /// <summary>
    /// Implements visual representation of user heart rate on the AR display.
    /// Subscribes to HeartRateEvent via the WidgetEventBus.
    /// Derived from Sprint 4 Requirement R18 (Heart rate widget rendering).
    /// </summary>
    public class HeartRateWidget : BaseWidget
    {
        [Header("Heart Rate UI")]
        // [SerializeField] private TMPro.TextMeshProUGUI bpmText;
        [SerializeField] private Color normalColor = Color.white;
        [SerializeField] private Color elevatedColor = Color.red;
        [SerializeField] private Color disconnectedColor = Color.gray;
        
        private int currentBpm = 0;
        private float currentConfidence = 0;
        
        private HUDLink.Utils.DataSmoother bpmSmoother;
        private bool isConnected = true;
        private float lastDataTime = 0f;

        public override void Initialize()
        {
            base.Initialize();
            bpmSmoother = new HUDLink.Utils.DataSmoother(2f); // Initialize smoother (FR-4.2)
            
            // Subscribe to the global data pipeline for heart rate events
            WidgetEventBus.Subscribe<HeartRateEvent>(OnHeartRateUpdate);
            // Subscribe to connection drops (FR-4.1)
            HUDLink.Events.GlobalEventBus.Subscribe<HUDLink.Network.ConnectionStatusEvent>(OnConnectionUpdate);
            Debug.Log($"[{WidgetId}] HeartRateWidget Initialized and Subscribed.");
        }

        public override void DestroyWidget()
        {
            // Always unsubscribe to prevent memory leaks in the event bus
            WidgetEventBus.Unsubscribe<HeartRateEvent>(OnHeartRateUpdate);
            HUDLink.Events.GlobalEventBus.Unsubscribe<HUDLink.Network.ConnectionStatusEvent>(OnConnectionUpdate);
            base.DestroyWidget();
        }

        private void OnConnectionUpdate(HUDLink.Network.ConnectionStatusEvent connEvent)
        {
            isConnected = connEvent.IsConnected;
            if (!isConnected)
            {
                Debug.Log($"[{WidgetId}] Connection lost. Entering fallback state.");
            }
            UpdateVisuals();
        }

        private void OnHeartRateUpdate(HeartRateEvent hrEvent)
        {
            currentBpm = hrEvent.BPM;
            currentConfidence = hrEvent.Confidence;
            lastDataTime = Time.time;
            
            bpmSmoother.SetTarget(currentBpm);
            // Data event signifies a heartbeat update, but we rely on RenderWidget for smoothed UI output
        }

        protected override void RenderWidget(float deltaTime)
        {
            if (isConnected && Time.time - lastDataTime > 3f && lastDataTime > 0)
            {
                // Safety check: assumed disconnected if no data for 3 seconds
                isConnected = false;
                Debug.Log($"[{WidgetId}] Data stale. Entering fallback state.");
                UpdateVisuals();
            }

            float smoothedBpm = bpmSmoother?.Update(deltaTime) ?? 0;
            // In a real implementation this is where text element text/color gets explicitly set each frame
            // e.g., UpdateText(Mathf.RoundToInt(smoothedBpm));

            // Passive animations or visual pulses based on BPM could happen here
            // It operates within the maxDrawCalls budget defined in BaseWidget.
        }

        private void UpdateVisuals()
        {
            // Example AR text update:
            // if (bpmText != null) {
            //     if (!isConnected) {
            //         bpmText.color = disconnectedColor;
            //         bpmText.text = "-- BPM";
            //     } else {
            //         bpmText.color = currentBpm > 120 ? elevatedColor : normalColor;
            //         bpmText.text = $"{currentBpm} BPM";
            //     }
            // }
            
            Debug.Log($"[{WidgetId}] Heart Rate Visual Updated: {currentBpm} BPM (Conf: {currentConfidence}) | Connected: {isConnected}");
        }
    }
}
