package com.waben.stock.datalayer.manage.repository.impl;

import com.waben.stock.datalayer.manage.entity.OperationLog;
import com.waben.stock.datalayer.manage.repository.OperationLogDao;
import com.waben.stock.datalayer.manage.repository.impl.jpa.OperationLogRepository;
import com.waben.stock.datalayer.manage.repository.impl.jpa.PermissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: zengzhiwei
 * @date: 2018/7/27 15:41
 * @desc：操作日志
 */
@Repository
public class OperationLogDaoImpl implements OperationLogDao {

    @Autowired
    private OperationLogRepository repository;

    @Override
    public OperationLog create(OperationLog operationLog) {
        return repository.save(operationLog);
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public OperationLog update(OperationLog operationLog) {
        return null;
    }

    @Override
    public OperationLog retrieve(Long id) {
        return null;
    }

    @Override
    public Page<OperationLog> page(int page, int limit) {
        return null;
    }

    @Override
    public Page<OperationLog> page(Specification<OperationLog> specification, Pageable pageable) {
        return null;
    }

    @Override
    public List<OperationLog> list() {
        return null;
    }
}
