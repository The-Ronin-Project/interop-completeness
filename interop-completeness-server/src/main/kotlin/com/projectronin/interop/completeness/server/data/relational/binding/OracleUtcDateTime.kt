package com.projectronin.interop.completeness.server.data.relational.binding

import oracle.sql.TIMESTAMP
import org.ktorm.schema.BaseTable
import org.ktorm.schema.Column
import org.ktorm.schema.SqlType
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.sql.Types
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Reads a time into a UTC-based OffsetDateTime.
 */
fun BaseTable<*>.oracleUtcDateTime(name: String): Column<OffsetDateTime> = registerColumn(name, OracleUTCDateTimeSqlType)

/**
 * SqlType supporting storing an OffsetDateTime relative to UTC in an Oracle DB.
 */
object OracleUTCDateTimeSqlType : SqlType<OffsetDateTime>(Types.TIMESTAMP, "datetime") {
    override fun doGetResult(
        rs: ResultSet,
        index: Int,
    ): OffsetDateTime? {
        // Oracle uses TIMESTAMP which does not extend Timestamp, so this is a workaround to work for Oracle AND not Oracle (like our unit tests use)
        val timestamp =
            when (val fieldValue = rs.getObject(index)) {
                null -> null
                is TIMESTAMP -> fieldValue.timestampValue()
                else -> rs.getTimestamp(index)
            }

        return timestamp?.let {
            OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC)
        }
    }

    override fun doSetParameter(
        ps: PreparedStatement,
        index: Int,
        parameter: OffsetDateTime,
    ) {
        ps.setTimestamp(index, Timestamp.from(parameter.toInstant()))
    }
}
