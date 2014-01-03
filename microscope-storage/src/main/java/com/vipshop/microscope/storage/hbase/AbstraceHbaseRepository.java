package com.vipshop.microscope.storage.hbase;

import java.io.IOException;
import java.util.NavigableMap;

import javax.annotation.Resource;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.hfile.Compression.Algorithm;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.stereotype.Component;

@Component
public abstract class AbstraceHbaseRepository implements InitializingBean {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstraceHbaseRepository.class);
	
	@Resource(name = "hbaseConfiguration")
	protected Configuration config;

	@Autowired
	protected HbaseTemplate hbaseTemplate;

	protected HBaseAdmin admin;

	@Override
	public void afterPropertiesSet() throws Exception {
		admin = new HBaseAdmin(config);
	}
	
	public void initialize(String tableName, String cfName) { 
		try {
			if (!admin.tableExists(tableName)) {
				HTableDescriptor tableDescriptor = new HTableDescriptor(tableName);
				HColumnDescriptor columnDescriptor = new HColumnDescriptor(cfName);
				columnDescriptor.setMaxVersions(1);
				columnDescriptor.setCompressionType(Algorithm.SNAPPY);
				tableDescriptor.addFamily(columnDescriptor);
				
				admin.createTable(tableDescriptor);
				
				logger.info("init hbase table " + tableName);
			}
		} catch (IOException e) {
			throw new RuntimeException("initialize " + tableName, e);
		}
	}
	
	public void drop(String tableName) {
		byte[] tableNameAsBytes = Bytes.toBytes(tableName);
		try {
			if (!admin.isTableDisabled(tableNameAsBytes)) {
				admin.disableTable(tableNameAsBytes);
			}
			admin.deleteTable(tableNameAsBytes);
			
			logger.info("drop hbase table " + tableName);
		} catch (IOException e) {
			throw new RuntimeException("drop" + tableName, e);
		}
	}
	
	public Configuration getConfiguration() {
		return config;
	}
	
	public String[] getColumnsInColumnFamily(Result r, String ColumnFamily) {
		NavigableMap<byte[], byte[]> familyMap = r.getFamilyMap(Bytes.toBytes(ColumnFamily));
		String[] Quantifers = new String[familyMap.size()];

		int counter = 0;
		for (byte[] bQunitifer : familyMap.keySet()) {
			Quantifers[counter++] = Bytes.toString(bQunitifer);

		}
		return Quantifers;
	}

}
