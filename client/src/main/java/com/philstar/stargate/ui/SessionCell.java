package com.philstar.stargate.ui;

import com.philstar.stargate.AppState;
import com.philstar.stargate.proto.ChatSession;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Custom ListCell that renders a conversation session:
 *   ┌──────────────────────────────────────┐
 *   │ Contact name / phone       [OPEN]    │
 *   │ Group name                           │
 *   └──────────────────────────────────────┘
 */
public class SessionCell extends ListCell<ChatSession> {

    private final VBox  root;
    private final Label nameLabel;
    private final Label groupLabel;
    private final Label statusLabel;

    public SessionCell() {
        nameLabel   = new Label();
        nameLabel.getStyleClass().add("session-name");
        HBox.setHgrow(nameLabel, Priority.ALWAYS);

        statusLabel = new Label();
        statusLabel.getStyleClass().add("session-status");

        HBox header = new HBox(nameLabel, new Region(), statusLabel);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);
        header.setSpacing(4);

        groupLabel  = new Label();
        groupLabel.getStyleClass().add("session-group");

        root = new VBox(3, header, groupLabel);
        root.setPadding(new Insets(8, 12, 8, 12));
        root.setMaxWidth(Double.MAX_VALUE);
    }

    @Override
    protected void updateItem(ChatSession session, boolean empty) {
        super.updateItem(session, empty);
        if (empty || session == null) {
            setGraphic(null);
            return;
        }

        AppState state = AppState.get();
        nameLabel.setText(state.getContactDisplayName(session.getContactPhone()));
        groupLabel.setText(state.getGroupName(session.getGroupId()));
        statusLabel.setText(session.getStatus());

        statusLabel.getStyleClass().removeAll("status-open", "status-closed");
        statusLabel.getStyleClass().add(
                "OPEN".equals(session.getStatus()) ? "status-open" : "status-closed");

        setGraphic(root);
    }
}
