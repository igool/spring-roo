package org.springframework.roo.project;

import org.apache.commons.lang3.Validate;
import org.springframework.roo.file.monitor.FileMonitorService;
import org.springframework.roo.file.monitor.MonitoringRequest;
import org.springframework.roo.file.undo.UndoManager;
import org.springframework.roo.file.undo.UndoableOperation;

/**
 * Allows {@link org.springframework.roo.file.monitor.MonitoringRequest}s to be
 * applied as {@link org.springframework.roo.file.undo.UndoableOperation}s.
 * 
 * @author Ben Alex
 * @since 1.0
 */
public class UndoableMonitoringRequest implements UndoableOperation {

    private final boolean add;
    private final FileMonitorService fileMonitorService;
    private final MonitoringRequest monitoringRequest;
    private boolean resetRequired;

    public UndoableMonitoringRequest(final UndoManager undoManager,
            final FileMonitorService fileMonitorService,
            final MonitoringRequest monitoringRequest, final boolean add) {
        Validate.notNull(undoManager, "Undo manager required");
        Validate.notNull(fileMonitorService, "File monitor service required");
        Validate.notNull(monitoringRequest, "Request required");
        this.fileMonitorService = fileMonitorService;
        this.monitoringRequest = monitoringRequest;
        this.add = add;

        if (add) {
            resetRequired = fileMonitorService.add(monitoringRequest);
        }
        else {
            resetRequired = fileMonitorService.remove(monitoringRequest);
        }

        undoManager.add(this);
    }

    public void reset() {
    }

    public boolean undo() {
        if (!resetRequired) {
            return true;
        }
        try {
            if (add) {
                fileMonitorService.remove(monitoringRequest);
            }
            else {
                fileMonitorService.add(monitoringRequest);
            }
            return true;
        }
        catch (final RuntimeException e) {
            return false;
        }
    }
}
