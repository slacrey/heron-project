package com.loadburn.heron.storage.transaction.impl;

import com.loadburn.heron.storage.exceptions.TransactionException;
import com.loadburn.heron.storage.transaction.InTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author slacrey (scstlinfeng@yahoo.com)
 *         Date: 13-12-12
 */
public class JdbcTransactionImpl implements InTransaction {

    private final Logger log = LoggerFactory.getLogger(JdbcTransactionImpl.class.getName());
    private final Connection connection;
    private boolean toggleAutoCommit;
    private boolean begun;
    private boolean committed;
    private boolean rolledBack;
    private boolean commitFailed;

    public JdbcTransactionImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void begin() {
        if (begun) {
            return;
        }
        if (commitFailed) {
            throw new TransactionException("cannot re-start transaction after failed commit");
        }

        log.debug("begin");
        try {
            toggleAutoCommit = connection.getAutoCommit();
            if (log.isDebugEnabled()) {
                log.debug("current autocommit status: " + toggleAutoCommit);
            }
            if (toggleAutoCommit) {
                log.debug("disabling autocommit");
                connection.setAutoCommit(false);
            }
        } catch (SQLException e) {
            log.error("JDBC begin failed", e);
            throw new TransactionException("JDBC begin failed: ", e);
        }

        begun = true;
        committed = false;
        rolledBack = false;

    }

    @Override
    public void commit() {
        if (!begun) {
            throw new TransactionException("Transaction not successfully started");
        }

        log.debug("commit");

        try {
            commitAndResetAutoCommit();
            log.debug("committed JDBC Connection");
            committed = true;
        } catch (SQLException e) {
            log.error("JDBC commit failed", e);
            commitFailed = true;
            throw new TransactionException("JDBC commit failed", e);
        }
    }

    @Override
    public void rollback() {
        if (!begun && !commitFailed) {
            throw new TransactionException("Transaction not successfully started");
        }

        log.debug("rollback");

        if (!commitFailed) {
            try {
                rollbackAndResetAutoCommit();
                log.debug("rolled back JDBC Connection");
                rolledBack = true;
            } catch (SQLException e) {
                log.error("JDBC rollback failed", e);
                throw new TransactionException("JDBC rollback failed", e);
            }
        }
    }

    @Override
    public boolean isActive() {
        return begun && !(rolledBack || committed | commitFailed);
    }

    // =========================================================
    private void toggleAutoCommit() {
        try {
            if (toggleAutoCommit) {
                log.debug("re-enabling autocommit");
                connection.setAutoCommit(true);
            }
        } catch (Exception sqle) {
            log.error("Could not toggle autocommit", sqle);
        }
    }

    private void commitAndResetAutoCommit() throws SQLException {
        try {
            connection.commit();
        } finally {
            toggleAutoCommit();
        }
    }

    private void rollbackAndResetAutoCommit() throws SQLException {
        try {
            connection.rollback();
        } finally {
            toggleAutoCommit();
        }
    }

}
