package com.projectronin.interop.completeness.server.data.relational.binding

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import oracle.sql.TIMESTAMP
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.ktorm.dsl.QueryRowSet
import org.ktorm.schema.BaseTable
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset

class OracleUTCDateTimeTest {
    @Test
    fun `sqlType supports getting null field`() {
        val resultSet =
            mockk<ResultSet> {
                every { getObject(1) } returns null
                every { wasNull() } returns true
            }

        val offsetDateTime = OracleUTCDateTimeSqlType.getResult(resultSet, 1)
        assertNull(offsetDateTime)
    }

    @Test
    fun `sqlType supports getting non-null SQL Timestamp`() {
        val timestamp =
            Timestamp.from(
                LocalDateTime.of(2023, 4, 10, 11, 25, 30, 0).toInstant(
                    ZoneOffset.ofHours(3),
                ),
            )
        val resultSet =
            mockk<ResultSet> {
                every { getObject(1) } returns timestamp
                every { getTimestamp(1) } returns timestamp
                every { wasNull() } returns false
            }

        val offsetDateTime = OracleUTCDateTimeSqlType.getResult(resultSet, 1)
        assertEquals(OffsetDateTime.of(2023, 4, 10, 8, 25, 30, 0, ZoneOffset.UTC), offsetDateTime)
    }

    @Test
    fun `sqlType supports getting non-null Oracle Timestamp`() {
        val resultSet =
            mockk<ResultSet> {
                every { getObject(1) } returns
                    TIMESTAMP(
                        Timestamp.from(
                            LocalDateTime.of(2023, 4, 10, 11, 25, 30, 0).toInstant(
                                ZoneOffset.ofHours(3),
                            ),
                        ),
                    )
                every { wasNull() } returns false
            }

        val offsetDateTime = OracleUTCDateTimeSqlType.getResult(resultSet, 1)
        assertEquals(OffsetDateTime.of(2023, 4, 10, 8, 25, 30, 0, ZoneOffset.UTC), offsetDateTime)
    }

    @Test
    fun `sqlType supports writing OffsetDateTime`() {
        val preparedStatement = mockk<PreparedStatement>(relaxed = true)
        OracleUTCDateTimeSqlType.setParameter(
            preparedStatement,
            1,
            OffsetDateTime.of(2023, 4, 10, 8, 25, 30, 0, ZoneOffset.ofHours(-7)),
        )

        verify(exactly = 1) {
            preparedStatement.setTimestamp(
                1,
                Timestamp.from(LocalDateTime.of(2023, 4, 10, 15, 25, 30, 0).toInstant(ZoneOffset.UTC)),
            )
        }
    }

    @Test
    fun `oracleUTCDateTime registers column`() {
        val baseTable =
            object : BaseTable<Any>("table") {
                override fun doCreateEntity(
                    row: QueryRowSet,
                    withReferences: Boolean,
                ): Any {
                    TODO("Not yet implemented")
                }
            }

        val spiedBaseTable = spyk(baseTable)

        spiedBaseTable.oracleUtcDateTime("utc_dt_tm")

        verify(exactly = 1) { spiedBaseTable.registerColumn("utc_dt_tm", OracleUTCDateTimeSqlType) }
    }
}
