using UnityEngine;

namespace HUDLink.Widgets
{
    /// <summary>
    /// Abstract base class for all HUD widgets, providing standard implementation
    /// for lifecycle and performance tracking.
    /// Derived from Sprint 3 Requirement FR-3.1.
    /// </summary>
    public abstract class BaseWidget : MonoBehaviour, IWidget
    {
        [Header("Widget Configuration")]
        [SerializeField] private string widgetId;
        [SerializeField] private Vector2 layoutBounds = new Vector2(0.2f, 0.2f);
        
        [Header("Performance Constraints")]
        [SerializeField] private int maxDrawCalls = 5;
        
        protected bool isInitialized = false;
        protected bool isSuspended = false;
        
        [Header("Animation State")]
        protected bool isExpanded = true;
        private Vector3 targetScale = Vector3.one;

        public string WidgetId => string.IsNullOrEmpty(widgetId) ? gameObject.name : widgetId;

        public virtual void Initialize()
        {
            isInitialized = true;
            isSuspended = false;
        }

        public virtual void UpdateWidget(float deltaTime)
        {
            if (!isInitialized || isSuspended) return;
            
            // Smooth expand/minimize transitions (FR-4.4)
            transform.localScale = Vector3.Lerp(transform.localScale, targetScale, deltaTime * 8f);
            
            RenderWidget(deltaTime);
        }

        public virtual void ToggleExpand()
        {
            isExpanded = !isExpanded;
            targetScale = isExpanded ? Vector3.one : new Vector3(0.5f, 0.5f, 0.5f); // Simple scale for minimize
        }

        public virtual void Suspend()
        {
            isSuspended = true;
            gameObject.SetActive(false);
        }

        public virtual void Resume()
        {
            isSuspended = false;
            gameObject.SetActive(true);
        }

        public virtual void DestroyWidget()
        {
            isInitialized = false;
            Destroy(gameObject);
        }

        public Vector2 GetLayoutBounds()
        {
            return layoutBounds;
        }

        /// <summary>
        /// Derived classes implement specific rendering logic here,
        /// ensuring they stay within the maxDrawCalls budget.
        /// </summary>
        protected abstract void RenderWidget(float deltaTime);
    }
}
