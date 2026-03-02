using System;

namespace HUDLink.Widgets
{
    /// <summary>
    /// Formal widget lifecycle interface for HUD-Link.
    /// Derived from Sprint 3 Requirement FR-3.1.
    /// </summary>
    public interface IWidget
    {
        /// <summary>
        /// Unique identifier for the widget instance.
        /// </summary>
        string WidgetId { get; }

        /// <summary>
        /// Initializes the widget, allocating necessary resources.
        /// Expected to respect performance budgets (e.g., draw calls).
        /// </summary>
        void Initialize();

        /// <summary>
        /// Called when the widget should update its display data.
        /// Should operate within defined performance budgets.
        /// </summary>
        void UpdateWidget(float deltaTime);

        /// <summary>
        /// Suspends the widget, hiding it from view and pausing updates to save resources.
        /// </summary>
        void Suspend();

        /// <summary>
        /// Resumes the widget from a suspended state.
        /// </summary>
        void Resume();

        /// <summary>
        /// Destroys the widget, cleaning up any internal resources.
        /// </summary>
        void DestroyWidget();

        /// <summary>
        /// Returns the desired layout bounds of the widget (width, height).
        /// Required for the scalable dashboard placement.
        /// </summary>
        UnityEngine.Vector2 GetLayoutBounds();
    }
}
