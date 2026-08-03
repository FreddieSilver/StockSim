import { Line, LineChart, ReferenceLine, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts";
import type { PricePoint } from "../../types";

interface StockChartProps {
    data: PricePoint[];
    height?: number;
    yAxisWidth?: number;
}

export function StockChart({ data, height = 300, yAxisWidth = 60 }: StockChartProps) {
    return (
        <ResponsiveContainer width="100%" height={height}>
            <LineChart data={data}>
                <XAxis dataKey="timestamp" stroke="#64748b" fontSize={12} tickMargin={10} minTickGap={20} />
                <YAxis
                    domain={["dataMin", "dataMax"]}
                    stroke="#64748b"
                    fontSize={12}
                    tickFormatter={(val) => `$${Number(val).toFixed(2)}`}
                    width={yAxisWidth}
                />

                <Tooltip
                    contentStyle={{ backgroundColor: "#10151e", borderColor: "#303b4c", borderRadius: "8px" }}
                    itemStyle={{ color: "#8b5cf6" }}
                    formatter={(val: any) => [`$${Number(val).toFixed(2)}`, "Price"]}
                />

                {data.length > 0 && <ReferenceLine y={data[0]?.price} stroke="#303b4c" strokeDasharray="3 3" />}
                <Line type="monotone" dataKey="price" stroke="#8b5cf6" strokeWidth={2} dot={false} isAnimationActive={false} />
            </LineChart>
        </ResponsiveContainer>
    );
}

